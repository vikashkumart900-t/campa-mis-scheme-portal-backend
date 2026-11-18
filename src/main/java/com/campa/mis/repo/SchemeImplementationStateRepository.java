
package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeImplementationStateEntity;

public interface SchemeImplementationStateRepository extends JpaRepository<SchemeImplementationStateEntity, Long> {

	Optional<SchemeImplementationStateEntity> findBySchemeIdAndStateCode(Long schemeId, String stateCode);
}
