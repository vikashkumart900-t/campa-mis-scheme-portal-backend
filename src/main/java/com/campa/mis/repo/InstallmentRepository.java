package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.InstallmentEntity;

public interface InstallmentRepository
        extends JpaRepository<InstallmentEntity, Integer> {
}