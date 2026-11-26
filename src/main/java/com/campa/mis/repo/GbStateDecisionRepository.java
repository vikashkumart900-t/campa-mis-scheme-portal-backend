package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.GbStateDecisionEntity;

public interface GbStateDecisionRepository extends JpaRepository<GbStateDecisionEntity, Long> {

	List<GbStateDecisionEntity> findByGbDecisionId(Long gbDecisionId);

	void deleteByGbDecisionId(Long gbDecisionId);
}