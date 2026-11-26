package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.GbDecisionEntity;

public interface GbDecisionRepository extends JpaRepository<GbDecisionEntity, Long> {

	Optional<GbDecisionEntity> findBySchemeId(Long schemeId);
}