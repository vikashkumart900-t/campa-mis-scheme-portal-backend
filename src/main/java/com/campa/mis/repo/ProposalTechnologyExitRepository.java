package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeTechnologyExitEntity;

import java.util.List;
import java.util.Optional;

public interface ProposalTechnologyExitRepository extends JpaRepository<SchemeTechnologyExitEntity, Long> {

	Optional<SchemeTechnologyExitEntity> findBySchemeId(Long schemeId);
	
	 List<SchemeTechnologyExitEntity> findAllBySchemeId(Long schemeId);

}