package com.mysite.jgo.updown;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;


@RestController
@Log4j2
public class UpDownController {
	
	//application.properties 파일에 설정해 둔 저장경로의 값(C:\\upload)을 가져온다.
	@Value("${com.mysite.jgo.upload.path}")
	private String uploadPath;
	
	@PostMapping(value="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
		System.out.println("업다운 컨트롤러 메소드 실행");
		//List<UploadResultDTO> list = new ArrayList<>();
		
		if(uploadFileDTO.getFiles() !=null) {
			
			final List<UploadResultDTO> list = new ArrayList<>();
			
			uploadFileDTO.getFiles().forEach(multipartFile -> {
				//log.info("이미지파일 원본 이름: "+multipartFile.getOriginalFilename());
				String originalName = multipartFile.getOriginalFilename();
				//이미지 파일의 이름 중복을 피하기 위해 랜덤 문자열 생성 객체인 uuid를 사용
				String uuid = UUID.randomUUID().toString();
				
				Path savePath = Paths.get(uploadPath, uuid+"_"+originalName);
				
				boolean image = false;
				
				try {
					/*
					 * multipartFile.transferTo(savePath); String imageFilePath = "/upload/" + uuid
					 * + "_" + originalName; dashboard.addImage(uuid, originalName, imageFilePath);
					 * list.add(new UploadResultDTO(uuid, originalName, true));
					 */
					//이미지 파일이라면 저장
					if(Files.probeContentType(savePath).startsWith("image")) {
						image=true;
						multipartFile.transferTo(savePath); //실제 파일 저장
						//섬네일도 저장
						File thumbFile = new File(uploadPath, "s_"+uuid+"_"+originalName);
						Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
					}
					
					
				}catch(IOException e) {
					e.printStackTrace();
				}
				
				list.add(UploadResultDTO.builder().uuid(uuid).fileName(originalName).img(image).build());
				
			});
			return list;
		}
		return new ArrayList<>();
	}
	
	//첨부파일 조회
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {
		
		//파일 시스템에서 파일을 읽을 수 있는 resource 객체 생성
		Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
		System.out.println("resource : "+resource);
		
		String resourceName = resource.getFilename();
		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	//첨부파일 삭제
	@DeleteMapping("/remove/{fileName}")
	public Map<String,Boolean> removeFile(@PathVariable String fileName){
		
		Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
		
		String resourceName = resource.getFilename();
		
		
		Map<String, Boolean> resultMap = new HashMap<>();
		
		boolean removed = false;
		
		try {
			String contentType = Files.probeContentType(resource.getFile().toPath());
			
			if(contentType.startsWith("image")) {
				File thumbnailFile = new File(uploadPath+File.separator+"s_"+fileName);
				
				thumbnailFile.delete();
				removed =true;
			}
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		resultMap.put("result", removed);
		return resultMap;
	}
	
}
