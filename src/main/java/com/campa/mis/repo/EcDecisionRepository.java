package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.EcDecisionEntity;

public interface EcDecisionRepository extends JpaRepository<EcDecisionEntity, Long> {

	Optional<EcDecisionEntity> findBySchemeId(Long schemeId);
}