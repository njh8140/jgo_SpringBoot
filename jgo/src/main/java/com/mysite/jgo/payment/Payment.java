package com.mysite.jgo.payment;

import java.time.LocalDateTime;

import com.mysite.jgo.dashboard.Dashboard;
import com.mysite.jgo.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long payno;
	
	//외래키
	@OneToOne
	@JoinColumn(name = "dno")
	private Dashboard dashboard;
	
	//외래키
	@ManyToOne
	private Member member;
	
	private Long payno_mno;
	
	private Long status;
	
	private LocalDateTime cre_date;
	
}
