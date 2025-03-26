package com.mysite.jgo.dashboard;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {
	Page<Dashboard> findAll(Specification<Dashboard> spec, Pageable pageable);
	
	@EntityGraph(attributePaths = {"imageSet"})
	@Query("select b from Dashboard b where b.dno =:dno")
	Optional<Dashboard> findByIdWithImages(Integer dno);
}