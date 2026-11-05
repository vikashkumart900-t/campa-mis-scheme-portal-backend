package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.UserRoleEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository
        extends JpaRepository<UserRoleEntity, Long> {

	Optional<UserRoleEntity> findByRoleCodeAndIsActiveTrue(
            String roleCode
    );
	
	Optional<UserRoleEntity> findByRoleCode(String roleCode);
}