package com.mysite.jgo.dashboard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mysite.jgo.image.DashboardImage;
import com.mysite.jgo.member.Member;
import com.mysite.jgo.reply.Reply;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Dashboard {
	//엔티티 설정
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer dno;
	
	@Column(length = 225)
	private String subject;
	
	@Column(columnDefinition = "Text")//글자수 제한 X
	private String content;
	
	//게시글 종류(0팝니다/1삽니다)
	private Integer type;
	
	//게시글 상태(0판매중/1거래중/2판매완료/3구매글)
	private Integer status;
	
	@Column(length = 50)
	private String pCategory;
	
	private Integer pPrice;
	
	private LocalDateTime creDate;
	
	private LocalDateTime modDate;
	//외래키
	@ManyToOne
	private Member member;
	
	@OneToMany(mappedBy = "dashboard", cascade = CascadeType.REMOVE, orphanRemoval = true)
 	//private List<Reply> replyList;
	private List<Reply> replyList = new ArrayList<>();
	
	@OneToMany(mappedBy = "dashboard", cascade= {CascadeType.ALL}, fetch = FetchType.LAZY,
			orphanRemoval = true)
	@Builder.Default
	private Set<DashboardImage> imageSet = new HashSet<>();
	
	public void addImage(String uuid, String fileName, String filePath) {
		
		if (uuid == null) {
			 uuid = UUID.randomUUID().toString(); //  UUID 자동 생성
		}
		
	    DashboardImage dashboardImage = DashboardImage.builder()
	            .uuid(uuid)
	            .fileName(fileName)
	            .filePath(filePath)
	            .dashboard(this)
	            .ord(imageSet.size())
	            .build();
	    
	    this.imageSet.add(dashboardImage); // <-- 추가해야 이미지가 저장됨!
	    
	}
	
	public void deleteImage() {
		imageSet.forEach(dashboardImage -> dashboardImage.changeDashboard(null));
		this.imageSet.clear();
	}
	
}
