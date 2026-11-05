package com.campa.mis.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.campa.mis.entity.SubAgencyEntity;

public interface SubAgencyRepository extends JpaRepository<SubAgencyEntity,Long>{
	
	List<SubAgencyEntity> findByOrganizationId(Long organizationId);

}
