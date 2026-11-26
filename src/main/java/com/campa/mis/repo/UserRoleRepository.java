package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.UserRoleEntity;

public interface UserRoleRepository
        extends JpaRepository<UserRoleEntity, Long> {

	Optional<UserRoleEntity> findByRoleCodeAndIsActiveTrue(
            String roleCode
    );
	
	Optional<UserRoleEntity> findByRoleCode(String roleCode);
}