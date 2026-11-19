package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeEmploymentGenerationEntity;

public interface SchemeEmploymentGenerationRepository extends JpaRepository<SchemeEmploymentGenerationEntity, Long> {

	Optional<SchemeEmploymentGenerationEntity> findBySchemeId(Long schemeId);
	
	void deleteBySchemeId(Long schemeId);

}