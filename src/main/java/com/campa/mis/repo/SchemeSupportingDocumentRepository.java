package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeSupportingDocumentEntity;

import jakarta.transaction.Transactional;

public interface SchemeSupportingDocumentRepository extends JpaRepository<SchemeSupportingDocumentEntity, Long> {

	Optional<SchemeSupportingDocumentEntity> findBySchemeIdAndDocumentType(Long schemeId, String documentType);

	Optional<SchemeSupportingDocumentEntity> findBySchemeId(Long schemeId);

	@Modifying
	@Transactional
	@Query("DELETE FROM SchemeSupportingDocumentEntity d WHERE d.schemeId = :schemeId")
	int deleteBySchemeId(@Param("schemeId") Long schemeId);

}