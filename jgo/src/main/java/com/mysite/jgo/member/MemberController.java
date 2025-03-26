package com.mysite.jgo.member;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService ms;

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
    
    @GetMapping("/signup")
    public String signup(MemberCreateForm memberCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
        	ms.create(memberCreateForm.getId(), 
        		memberCreateForm.getPassword1(), memberCreateForm.getAddr(), 
        		memberCreateForm.getEmail(),memberCreateForm.getPhoneno(), 
        		memberCreateForm.getCre_date(), memberCreateForm.getMod_date());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
    
 
    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") String id, MemberUpdateForm memberUpdateForm) {
        Member member = ms.findById(id);  // MemberService에서 회원 정보 조회
        memberUpdateForm.setId(member.getId());
        memberUpdateForm.setAddr(member.getAddr());
        memberUpdateForm.setEmail(member.getEmail());
        memberUpdateForm.setPhoneno(member.getPhoneno());

        return "update_form"; 
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") String id, @Valid MemberUpdateForm memberUpdateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update_form";
        }
        try {
            ms.updateMember( 
            	memberUpdateForm.getId(), 
            	memberUpdateForm.getPassword1(), 
            	memberUpdateForm.getAddr(), 
            	memberUpdateForm.getEmail(), 
            	memberUpdateForm.getPhoneno());
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("updateFailed", "회원 수정에 실패했습니다.");
            return "update_form";
        }

        return "redirect:/member/login"; //어느 페이지로 갈지 의논 필요
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        try {
            ms.deleteMember(id);
        } catch (Exception e) {
            e.printStackTrace();
            //return "redirect:/member/profile?error=true"; // 삭제 실패시 오류 메시지
            return "redirect:/"; // 삭제 실패시 오류 메시지
        }

        return "redirect:/"; // 삭제 후 홈 페이지로 리다이렉트
    }
    
}