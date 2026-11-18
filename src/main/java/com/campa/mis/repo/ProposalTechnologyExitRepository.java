package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.ProposalTechnologyExitEntity;

import java.util.List;
import java.util.Optional;

public interface ProposalTechnologyExitRepository extends JpaRepository<ProposalTechnologyExitEntity, Long> {

	Optional<ProposalTechnologyExitEntity> findBySchemeId(Long schemeId);
	
	 List<ProposalTechnologyExitEntity> findAllBySchemeId(Long schemeId);

}