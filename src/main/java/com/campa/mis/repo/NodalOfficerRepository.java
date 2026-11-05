package com.campa.mis.repo;


import com.campa.mis.entity.NodalOfficerEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NodalOfficerRepository
        extends JpaRepository<NodalOfficerEntity, Long> {
	
	Optional<NodalOfficerEntity> findByEmail(String email);
}