package com.campa.mis.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.entity.SchemeEntity;

public interface SchemeRepository extends JpaRepository<SchemeEntity, Long> {

	List<SchemeEntity> findByIsActiveTrue();

	@Query(value = "SELECT\r\n" + "	s.scheme_name AS schemeName,\r\n"
			+ "			    o.organization_name AS organizationName,\r\n"
			+ "			    pd.division_name AS divisionName\r\n" + "	FROM mis_scheme.schemes s\r\n"
			+ "			LEFT JOIN mis_scheme.organizations o\r\n"
			+ "			    ON s.organization_id = o.organization_id\r\n"
			+ "			LEFT JOIN mis_scheme.program_divisions pd\r\n"
			+ "			    ON s.program_division_id = pd.program_division_id\r\n"
			+ "			WHERE s.scheme_id = :schemeId", nativeQuery = true)
	Optional<Object[]> findSchemeDetailsById(@Param("schemeId") Long schemeId);
}
