package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeFinalDocumentEntity;

public interface SchemeFinalDocumentRepository extends JpaRepository<SchemeFinalDocumentEntity, Long> {

	Optional<SchemeFinalDocumentEntity> findBySchemeId(Long schemeId);

}
