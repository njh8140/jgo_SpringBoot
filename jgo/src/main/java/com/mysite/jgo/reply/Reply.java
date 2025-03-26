package com.mysite.jgo.reply;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.mysite.jgo.dashboard.Dashboard;
import com.mysite.jgo.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rno;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@CreatedDate
	private LocalDateTime creDate;
	
	private LocalDateTime modDate;
	
	@ManyToOne
	private Dashboard dashboard;
	
	@ManyToOne
	private Member member;
	
	 //  생성 시간 자동 설정
    @PrePersist
    public void prePersist() {
        this.creDate = LocalDateTime.now();
    }
	
}
