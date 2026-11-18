
package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SchemeImplementationDistrictEntity;

public interface SchemeImplementationDistrictRepository
		extends JpaRepository<SchemeImplementationDistrictEntity, Long> {

	boolean existsBySchemeImplementationStateIdAndDistrictCode(Long schemeImplementationStateId, String districtCode);
}
