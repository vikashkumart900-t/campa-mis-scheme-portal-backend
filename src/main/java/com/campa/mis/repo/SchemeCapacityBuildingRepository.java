package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeCapacityBuildingEntity;

public interface SchemeCapacityBuildingRepository extends JpaRepository<SchemeCapacityBuildingEntity, Long> {

	Optional<SchemeCapacityBuildingEntity> findFirstBySchemeIdOrderByIdDesc(Long schemeId);
}