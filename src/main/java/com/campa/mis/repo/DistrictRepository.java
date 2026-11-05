package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.DistrictEntity;

public interface DistrictRepository extends JpaRepository<DistrictEntity,Long> {

	
	List<DistrictEntity> findByStateCode(String stateCode);
}
