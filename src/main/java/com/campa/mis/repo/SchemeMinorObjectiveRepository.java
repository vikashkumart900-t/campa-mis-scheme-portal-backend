package com.campa.mis.repo;

import com.campa.mis.entity.SchemeMinorObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SchemeMinorObjectiveRepository
        extends JpaRepository<SchemeMinorObjectiveEntity, Long> {

    List<SchemeMinorObjectiveEntity>
    findByObjectiveIdOrderByDisplayOrderAsc(Long objectiveId);

    void deleteByObjectiveIdIn(List<Long> objectiveIds);
}