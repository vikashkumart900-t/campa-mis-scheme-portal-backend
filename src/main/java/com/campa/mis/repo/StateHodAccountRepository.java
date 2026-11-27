package com.campa.mis.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.StateHodAccountEntity;

public interface StateHodAccountRepository extends JpaRepository<StateHodAccountEntity, Long> {

	boolean existsBySchemeIdAndStateCode(Long schemeId, String stateCode);

	List<StateHodAccountEntity> findBySchemeId(Long schemeId);

	List<StateHodAccountEntity> findBySchemeIdAndStateCodeIn(Long schemeId, List<String> stateCodes);

	Optional<StateHodAccountEntity> findBySchemeIdAndStateCode(Long schemeId, String stateCode);
}