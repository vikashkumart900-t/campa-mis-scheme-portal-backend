package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeEvaluationResearchEntity;

public interface SchemeEvaluationResearchRepository extends JpaRepository<SchemeEvaluationResearchEntity, Long> {

	Optional<SchemeEvaluationResearchEntity> findFirstBySchemeIdOrderByIdDesc(Long schemeId);

}