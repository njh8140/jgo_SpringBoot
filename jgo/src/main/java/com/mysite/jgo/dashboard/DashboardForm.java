package com.mysite.jgo.dashboard;

import java.time.LocalDateTime;

import com.mysite.jgo.member.Member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardForm {
	@NotEmpty(message = "제목은 필수항목입니다.")
	@Size(max = 225)
	private String subject;
	
	@NotEmpty(message = "내용은 필수항목입니다.")
	private String content;
	
	private Integer type;
	
	@NotEmpty(message = "카테고리 선택은 필수항목입니다.")
	private String pCategory;
	
	@NotNull(message = "가격은 필수항목입니다.")
	private Integer pPrice;
	
	private LocalDateTime creDate;
	
	private Member member;
	
}
