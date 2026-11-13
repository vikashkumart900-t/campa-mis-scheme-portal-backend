package com.campa.mis.repo;

import com.campa.mis.entity.SchemeObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SchemeObjectiveRepository
        extends JpaRepository<SchemeObjectiveEntity, Long> {

    List<SchemeObjectiveEntity>
    findBySchemeIdOrderByDisplayOrderAsc(Long schemeId);

    void deleteBySchemeId(Long schemeId);
}