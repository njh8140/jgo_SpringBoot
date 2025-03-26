package com.mysite.jgo.dashboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.jgo.member.Member;
import com.mysite.jgo.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
	
	private final DashboardService ds;
	private final MemberService ms;
	
	//리스트 페이지 호출
	@GetMapping("/list")
	public String list(Model model
			, @RequestParam(value="page", defaultValue="0") int page
			, @RequestParam(value="kw", defaultValue="") String kw) {
		Page<Dashboard> dashboardList = ds.getDashboardList(page, kw);
		model.addAttribute("paging", dashboardList);
		model.addAttribute("kw", kw);
		return "dashboard_list";
	}
	
	//게시글 등록 페이지 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String getDashboard(DashboardForm dashboardForm) {
		return "dashboard_form";
	}
	
	//게시글 등록 기능
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(@Valid DashboardForm df, BindingResult br, Principal principal) {
				if(br.hasErrors()){
					return "dashboard_form";
				}
				
				Member member = ms.getMember(principal.getName());
				ds.createDashboard(member,df.getSubject(),df.getContent(),df.getType(),df.getPCategory(),df.getPPrice(), null);
				return "redirect:/dashboard/list";
	}
	
	//게시글 상세화면 페이지 호출
	@GetMapping("/detail/{dno}")
	public String getDetail(Model model, @PathVariable(value="dno") Integer dno) {
		Dashboard dashboard = ds.getDashboard(dno);
		System.out.println("Replies: " + dashboard.getReplyList()); // 답변확인용
		model.addAttribute("dashboard",dashboard);
		return "dashboard_detail";
	}
	
	//Dashboard 수정 페이지 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{dno}")
	public String getModifyDashboard(DashboardForm dashboardForm, @PathVariable("dno") Integer dno, Principal principal) {
		//수정할 dashboard를 dno 값을 이용하여 가져온다.
		Dashboard dashboard = ds.getDashboard(dno);
		System.out.println("수정 버튼을 누른 회원의 id: "+dashboard.getMember().getId());
		System.out.println("principal 객체에서 불러 온 id: "+principal.getName());
		//게시글을 작성한 멤버의 id와 로그인 중인 회원의 id를 대조한다. 
		if(!dashboard.getMember().getId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		//게시글 상세화면에 보낼 정보를 dashboardForm에 담아야 한다.
		 dashboardForm.setSubject(dashboard.getSubject());
		 dashboardForm.setContent(dashboard.getContent());
		 dashboardForm.setPPrice(dashboard.getPPrice());
		 dashboardForm.setPCategory(dashboard.getPCategory());
		 //게시글 등록 화면을 호출
		 return "dashboard_form";
	}
	
	//Dashboard 수정 기능
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{dno}")
	public String postModifyDashboard(@Valid DashboardForm dashboardForm
			, BindingResult bindingresult
			, Principal principal
			, @PathVariable("dno") Integer dno) {
		if(bindingresult.hasErrors()) {
			return "dashboard_form";
		}
		Dashboard dashboard = ds.getDashboard(dno);
		if(!dashboard.getMember().getId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}else {
			ds.modify(dashboard, dashboardForm.getSubject(), dashboardForm.getContent(), dashboardForm.getPPrice(), dashboardForm.getPCategory(), null);
		}
		return "redirect:/dashboard/detail/"+dno;
	}
	
	//Dashboard 삭제 기능
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{dno}")
	public String dashboardDelete(Principal principal, @PathVariable("dno") Integer dno) {
		Dashboard dashboard = ds.getDashboard(dno);
		if(!dashboard.getMember().getId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}else {
			ds.delete(dashboard);
		}
		return "redirect:/";
	}
	
	public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("dno") Integer dno) {
	    if (file.isEmpty()) {
	        return "redirect:/dashboard/detail/" + dno + "?error=emptyFile";
	    }

	    // 파일 이름과 UUID 생성
	    String uuid = UUID.randomUUID().toString();
	    String fileName = file.getOriginalFilename();

	    // C:\\upload 폴더에 저장
	    String uploadDir = "C:\\upload"; // 원하는 경로
	    Path filePath = Paths.get(uploadDir, uuid + "_" + fileName);

	    try {
	        // C:\\upload 폴더에 파일 저장
	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	        
	        // static/images 폴더에 복사
	        String staticImagePath = "src/main/resources/static/images/" + uuid + "_" + fileName;
	        Files.copy(filePath, Paths.get(staticImagePath), StandardCopyOption.REPLACE_EXISTING);

	    } catch (IOException e) {
	        e.printStackTrace();
	        return "redirect:/dashboard/detail/" + dno + "?error=uploadFail";
	    }

	    // Dashboard에 이미지 정보 추가
	    Dashboard dashboard = ds.getDashboard(dno);
	    //dashboard.addImage(uuid, fileName);

	    return "redirect:/dashboard/detail/" + dno;
	}

	
}
