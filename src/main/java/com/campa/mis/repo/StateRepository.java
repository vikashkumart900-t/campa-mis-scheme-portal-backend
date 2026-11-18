package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.StateEntity;

public interface StateRepository extends JpaRepository<StateEntity, Long> {

	Optional<StateEntity> findByStateCode(String stateCode);

}
