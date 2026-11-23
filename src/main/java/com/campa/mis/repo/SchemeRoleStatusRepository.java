package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.SchemeRoleStatusEntity;

public interface SchemeRoleStatusRepository extends JpaRepository<SchemeRoleStatusEntity, Long> {

	Optional<SchemeRoleStatusEntity> findBySchemeId(Long schemeId);
}
