package com.mysite.jgo.image;

import com.mysite.jgo.dashboard.Dashboard;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "dashboard")
public class DashboardImage implements Comparable<DashboardImage> {
	
	//이미지 uuid
	@Id
	private String uuid;
	
	//등록된 이미지의 파일 이름
	private String fileName;
	// 경로 필드
	//private String filePath;
	
	//순서
	private int ord;
	
	//dashboard와 
	@ManyToOne
	private Dashboard dashboard;
	
	@Override
	public int compareTo(DashboardImage other) {
		return this.ord - other.ord;
	}
	
	public void changeDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}
	
	@Builder
    public DashboardImage(String uuid, String fileName, String filePath, Dashboard dashboard, Integer ord) {
        this.uuid = uuid;
        this.fileName = fileName;
        //this.filePath = filePath;
        this.dashboard = dashboard;
        this.ord = ord;
    }

	/*
	 * // 추가: 파일 경로 변경 public void changeFilePath(String filePath) { this.filePath =
	 * filePath; }
	 */
	
	
}
