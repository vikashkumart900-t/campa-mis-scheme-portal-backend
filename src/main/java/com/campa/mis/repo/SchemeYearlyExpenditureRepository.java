package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeYearlyExpenditureEntity;

public interface SchemeYearlyExpenditureRepository extends JpaRepository<SchemeYearlyExpenditureEntity, Long> {

	List<SchemeYearlyExpenditureEntity> findByDeliverableDetailsId(Long detailsId);

	@Modifying
	@Query("""
			    DELETE FROM SchemeYearlyExpenditureEntity e
			    WHERE e.deliverableDetailsId = :detailsId
			""")
	void deleteByDetailsId(@Param("detailsId") Long detailsId);
}