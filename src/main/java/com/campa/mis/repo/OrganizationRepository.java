package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.OrganizationEntity;

public interface OrganizationRepository
        extends JpaRepository<OrganizationEntity, Long> {
}