package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeEmploymentGenerationEntity;

public interface SchemeEmploymentGenerationRepository extends JpaRepository<SchemeEmploymentGenerationEntity, Long> {

	List<SchemeEmploymentGenerationEntity> findBySchemeId(Long schemeId);

}