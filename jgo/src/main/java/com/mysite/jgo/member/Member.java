package com.mysite.jgo.member;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long mno;
	
	@Column(unique = true)
	private String id;
	
	private String pw;
	
	private String addr;
	
	@Column(unique = true)
	private String email;
	
	@Column(unique = true)
	private String phoneno;
	
	private LocalDateTime cre_date;
	
	private LocalDateTime mod_date;
	
	// 엔티티가 처음 저장될 때 현재 시간 설정
    @PrePersist
    public void prePersist() {
    	LocalDateTime now = LocalDateTime.now();
    	if (this.cre_date == null) {
            this.cre_date = now;   // cre_date가 null인 경우에만 현재 시간으로 설정
        }
        this.mod_date = now;       // mod_date는 항상 현재 시간으로 설정
    }

    // 엔티티가 수정될 때 현재 시간 설정
    @PreUpdate
    public void preUpdate() {
        this.mod_date = LocalDateTime.now();
    }
}