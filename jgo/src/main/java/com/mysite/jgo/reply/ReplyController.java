package com.mysite.jgo.reply;


import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.jgo.dashboard.Dashboard;
import com.mysite.jgo.dashboard.DashboardService;
import com.mysite.jgo.member.Member;
import com.mysite.jgo.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Controller
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {

	private final DashboardService ds;
	private final MemberService ms;
	private final ReplyService rs;
	
	@PostMapping("/create/{dno}")
	public String createReply(Model model, 
								@PathVariable("dno") Integer dno,
								@RequestParam(name = "content") String content, 
								@Valid ReplyForm replyForm,BindingResult bindingResult,
								@AuthenticationPrincipal UserDetails userDetails) {
		Dashboard  dashboard = ds.getDashboard(dno);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("dashboard", dashboard);
			return "dashboard_detail";
		}
		
		Member member = ms.findById(userDetails.getUsername()); 
		rs.create(dashboard, content, member);
		
		return "redirect:/dashboard/detail/" +dno;
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{rno}")
	public String replyModify(ReplyForm replyForm, @PathVariable("rno") Integer rno, Principal principal) {
	       Reply reply = rs.getReply(rno);
	       if (!reply.getMember().getId().equals(principal.getName())) {
	           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
	       }
	       replyForm.setContent(reply.getContent());
	       return "reply_form";
	    }
	
	
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{rno}")
  public String replyModify(@Valid ReplyForm replyForm, BindingResult bindingResult,
          @PathVariable("rno") Integer rno, Principal principal) {
      if (bindingResult.hasErrors()) {
          return "reply_form";
      }
      Reply reply = rs.getReply(rno);
      if (!reply.getMember().getId().equals(principal.getName())) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
      }
      rs.modify(reply, replyForm.getContent());
      return String.format("redirect:/dashboard/detail/%s", reply.getDashboard().getDno());
  }
  
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{rno}")
  public String replyDelete(Principal principal, @PathVariable("rno") Integer rno) {
      Reply reply = rs.getReply(rno);
      if (!reply.getMember().getId().equals(principal.getName())) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
      }
      this.rs.delete(reply);
      return String.format("redirect:/dashboard/detail/%s", reply.getDashboard().getDno());
  }
}