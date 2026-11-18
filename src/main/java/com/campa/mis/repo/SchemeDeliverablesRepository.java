package com.campa.mis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeDeliverablesEntity;

public interface SchemeDeliverablesRepository extends JpaRepository<SchemeDeliverablesEntity, Long> {

	List<SchemeDeliverablesEntity> findByDeliverableDetailsId(Long detailsId);

	@Modifying
	@Query("""
			    DELETE FROM SchemeDeliverablesEntity d
			    WHERE d.deliverableDetailsId = :detailsId
			""")
	void deleteByDetailsId(@Param("detailsId") Long detailsId);
}