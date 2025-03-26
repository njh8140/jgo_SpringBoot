package com.mysite.jgo.dashboard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.jgo.DataNotFoundException;
import com.mysite.jgo.image.DashboardImage;
import com.mysite.jgo.member.Member;
import com.mysite.jgo.reply.Reply;
import com.mysite.jgo.updown.UploadResultDTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DashboardService {
	
	private final DashboardRepository dr;
	
	//게시글 등록
	public void createDashboard(
			Member member
			, String subject
			, String content
			, Integer type
			, String pCategory
			, Integer pPrice
			, List<UploadResultDTO> uploadedImages) {
		Dashboard dashboard = new Dashboard();
		//dno는 auto Increment 특성으로 자동 생성
		dashboard.setSubject(subject);
		dashboard.setContent(content);
		dashboard.setType(type);// 0:판매글 1:구매글
		dashboard.setPCategory(pCategory);
		dashboard.setPPrice(pPrice);
		dashboard.setCreDate(LocalDateTime.now());
		dashboard.setMember(member);//외래키 받기
		if(dashboard.getType() == 0) {
			dashboard.setStatus(0);
		}else {
			dashboard.setStatus(3);
		}
		// 이미지 목록 처리
	    if (uploadedImages != null && !uploadedImages.isEmpty()) {
	        List<DashboardImage> images = new ArrayList<>();
	        for (UploadResultDTO imageDTO : uploadedImages) {
	            DashboardImage dashboardImage = new DashboardImage();
	            dashboardImage.setUuid(imageDTO.getUuid());
	            dashboardImage.setFileName(imageDTO.getFileName());
	            dashboardImage.setOrd(images.size());  // 이미지 순서
	            dashboardImage.setDashboard(dashboard);  // 게시글과 연결
	            images.add(dashboardImage);
	        }
	    }
		dr.save(dashboard);
	}
	
	//대시보드 리스트 메소드
	public Page<Dashboard> getDashboardList(int page, String kw){
		//최신순으로 정렬
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("creDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Dashboard> spec = search(kw);
		return dr.findAll(spec, pageable);
		
	}
	
	//대시보드 세부 정보 메소드
	public Dashboard getDashboard(Integer dno) {
		Optional<Dashboard> dashboard = dr.findById(dno);
		if(dashboard.isPresent()) {
			
			return dashboard.get();
		}else {
			throw new DataNotFoundException("대시보드가 없습니다.");
		}
	}
	
	//대시보드 세부정보 수정
	public void modify(Dashboard dashboard, String subject, String content, Integer pPrice, String pCategory,
			List<UploadResultDTO> uploadedImages) {
		dashboard.setSubject(subject);
		dashboard.setContent(content);
		dashboard.setPPrice(pPrice);
		dashboard.setPCategory(pCategory);
		dashboard.setModDate(LocalDateTime.now());
		
		// 이미지 추가 처리
	    if (uploadedImages != null && !uploadedImages.isEmpty()) {
	        List<DashboardImage> images = new ArrayList<>();
	        for (UploadResultDTO imageDTO : uploadedImages) {
	            DashboardImage dashboardImage = new DashboardImage();
	            dashboardImage.setUuid(imageDTO.getUuid());
	            dashboardImage.setFileName(imageDTO.getFileName());
	            dashboardImage.setOrd(images.size());
	            dashboardImage.setDashboard(dashboard);
	            images.add(dashboardImage);
	        }
	    }
		dr.save(dashboard);
	}
	
	//대시보드 삭제
	public void delete(Dashboard dashboard) {
		dr.delete(dashboard);
	}
	
	//검색
	
	private Specification<Dashboard> search(String kw){
		return new Specification<>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<Dashboard> d, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Join<Dashboard, Member> u1 = d.join("member", JoinType.LEFT);
				//reply 만들면 주석 해제 처리할 것임
				Join<Dashboard, Reply> r = d.join("replyList", JoinType.LEFT);
				Join<Reply, Member> u2 = r.join("member", JoinType.LEFT);
				
				return cb.or(cb.like(d.get("subject"), "%"+kw+"%")// 게시글 제목
						, cb.like(d.get("content"), "%"+kw+"%") //게시글 내용
						, cb.like(d.get("pCategory"), "%"+kw+"%")//게시글 카테고리
						, cb.like(u1.get("id"), "%"+kw+"%") //게시글 작성자
						, cb.like(r.get("content"), "%"+kw+"%") // 답변 내용
						, cb.like(u2.get("id"), "%"+kw+"%") //답변 작성자
						);
			}
		};
	}
	
	/*
	 * //게시판 올리고 이미지 db 저장
	 * 
	 * @Transactional public void addImageToDashboard(Dashboard dashboard, String
	 * uuid, String fileName) { dashboard.addImage(uuid, fileName); }
	 */
}
