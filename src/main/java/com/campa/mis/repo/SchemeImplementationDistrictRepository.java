
package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeImplementationDistrictEntity;

public interface SchemeImplementationDistrictRepository
		extends JpaRepository<SchemeImplementationDistrictEntity, Long> {

	// boolean existsBySchemeImplementationStateIdAndDistrictCode(Long
	// schemeImplementationStateId, String districtCode);

	List<SchemeImplementationDistrictEntity> findBySchemeImplementationStateId(Long stateMappingId);

	void deleteBySchemeImplementationStateIdIn(List<Long> stateMappingIds);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(value = """
			DELETE FROM mis_scheme.scheme_implementation_districts
			WHERE scheme_implementation_state_id IN (:stateIds)
			""", nativeQuery = true)
	int deleteDistrictsByStateIds(@Param("stateIds") List<Long> stateIds);
}
