package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeSupportingDocumentEntity;

public interface SchemeSupportingDocumentRepository extends JpaRepository<SchemeSupportingDocumentEntity, Long> {

	Optional<SchemeSupportingDocumentEntity> findBySchemeIdAndDocumentType(Long schemeId, String documentType);

	Optional<SchemeSupportingDocumentEntity> findBySchemeId(Long schemeId);
	
	void deleteBySchemeId(Long schemeId);

}