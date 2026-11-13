package com.campa.mis.repo;

import com.campa.mis.entity.SchemeStateFundingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchemeStateFundingRepository
        extends JpaRepository<SchemeStateFundingEntity, Long> {

    Optional<SchemeStateFundingEntity>
    findBySchemeId(Long schemeId);
}