package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeSupportingDocumentEntity;

public interface SchemeSupportingDocumentRepository extends JpaRepository<SchemeSupportingDocumentEntity, Long> {

	List<SchemeSupportingDocumentEntity> findBySchemeId(Long schemeId);

}