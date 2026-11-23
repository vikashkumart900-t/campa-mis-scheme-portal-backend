
package com.campa.mis.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeImplementationStateEntity;

public interface SchemeImplementationStateRepository extends JpaRepository<SchemeImplementationStateEntity, Long> {

	List<SchemeImplementationStateEntity> findBySchemeId(Long schemeId);

	Optional<SchemeImplementationStateEntity> findBySchemeIdAndStateCode(Long schemeId, String stateCode);

	void deleteBySchemeId(Long schemeId);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(value = """
			DELETE FROM mis_scheme.scheme_implementation_states
			WHERE id IN (:stateIds)
			""", nativeQuery = true)
	int deleteStatesByIds(@Param("stateIds") List<Long> stateIds);
}
