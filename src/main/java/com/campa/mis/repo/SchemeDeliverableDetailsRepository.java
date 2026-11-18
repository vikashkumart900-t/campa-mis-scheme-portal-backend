package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeDeliverableDetailsEntity;

public interface SchemeDeliverableDetailsRepository extends JpaRepository<SchemeDeliverableDetailsEntity, Long> {

	Optional<SchemeDeliverableDetailsEntity> findBySchemeId(Long schemeId);

	boolean existsBySchemeId(Long schemeId);
}