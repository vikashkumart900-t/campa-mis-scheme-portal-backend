package com.campa.mis.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.ProposalListingProjectionResponse;
import com.campa.mis.dto.response.NodalOfficerDetailsResponse;
import com.campa.mis.entity.NodalOfficerEntity;

public interface NodalOfficerRepository extends JpaRepository<NodalOfficerEntity, Long> {

	Optional<NodalOfficerEntity> findByEmail(String email);

	Optional<NodalOfficerEntity> findBySchemeId(Long schemeId);

	@Query("""
			    SELECT
			        ndo.schemeId AS schemeId,

			        FUNCTION('split_part', sc.schemeName, ':', 1) AS schemeName,

			        ndo.schemeDuration AS schemeDuration,
			        ndo.proposedFinancialOutlay AS proposedFinancialOutlay,

			        CONCAT(
			            'PROP/',
			            CAST(YEAR(CURRENT_DATE) AS string),
			            '/',
			            CAST(ndo.schemeId AS string)
			        ) AS proposalId,

			        srs.hodStatus AS hodStatus,
			        srs.pdStatus AS pdStatus,
			        srs.campaStatus AS campaStatus

			    FROM NodalOfficerEntity ndo

			    INNER JOIN SchemeEntity sc
			        ON sc.schemeId = ndo.schemeId

			    LEFT JOIN SchemeRoleStatusEntity srs
			        ON srs.schemeId = ndo.schemeId

			    WHERE ndo.schemeId = :schemeId
			""")
	Optional<ProposalListingProjectionResponse> findProposalDetailsBySchemeId(@Param("schemeId") Long schemeId);

	@Query("""
			    SELECT

			         ndo.schemeId AS schemeId,
			        sc.schemeName AS schemeName,
			        ndo.schemeDuration AS schemeDuration,
			        ndo.proposedFinancialOutlay AS proposedFinancialOutlay,

			        CONCAT(
			            'PROP/',
			            CAST(YEAR(CURRENT_DATE) AS string),
			            '/',
			            CAST(ndo.schemeId AS string)
			        ) AS proposalId,

			        srs.hodStatus AS hodStatus,
			        srs.pdStatus AS pdStatus,
			        srs.campaStatus AS campaStatus

			    FROM NodalOfficerEntity ndo

			    INNER JOIN SchemeEntity sc
			        ON sc.schemeId = ndo.schemeId

			    LEFT JOIN SchemeRoleStatusEntity srs
			        ON srs.schemeId = ndo.schemeId
			""")
	List<ProposalListingProjectionResponse> findAllProposalDetails();

	@Query(value = """
			SELECT
			    ndo.officer_name AS "officerName",
			    ndo.designation AS "designation",
			    ndo.email AS "email",
			    ndo.mobile AS "mobile",
			    ndo.aadhaar_number AS "aadhaarNumber",
			    ndo.date_of_proposal AS "dateOfProposal",
			    ndo.expected_completion_date AS "expectedCompletionDate",
			    ndo.proposed_financial_outlay AS "proposedFinancialOutlay",
			    ndo.scheme_duration AS "schemeDuration",

			    (
			        SELECT STRING_AGG(
			            state_group.state_name || ': ' || state_group.district_names,
			            CHR(10)
			            ORDER BY state_group.state_name
			        )
			        FROM (
			            SELECT
			                st.state_name,

			                STRING_AGG(
			                    DISTINCT dt.district_name,
			                    ', '
			                    ORDER BY dt.district_name
			                ) AS district_names

			            FROM mis_scheme.scheme_implementation_states sis

			            INNER JOIN mis_scheme.scheme_implementation_districts sid
			                ON sid.scheme_implementation_state_id = sis.id

			            INNER JOIN mis_scheme.states st
			                ON st.state_code = sis.state_code

			            INNER JOIN mis_scheme.districts dt
			                ON dt.district_code = sid.district_code

			            WHERE sis.scheme_id = ndo.scheme_id

			            GROUP BY st.state_name

			        ) state_group

			    ) AS "schemeImplementingStates"

			FROM mis_scheme.nodal_officers ndo

			WHERE ndo.scheme_id = :schemeId

			ORDER BY ndo.nodal_officer_id
			""", nativeQuery = true)
	List<NodalOfficerDetailsResponse> findNodalOfficerDetailsBySchemeId(@Param("schemeId") Long schemeId);

	@Query(value = """
			SELECT
			    st.state_name AS "stateName",
			    dt.district_name AS "districtName"
			FROM mis_scheme.scheme_implementation_states sis

			INNER JOIN mis_scheme.scheme_implementation_districts sid
			    ON sid.scheme_implementation_state_id = sis.id

			INNER JOIN mis_scheme.states st
			    ON st.state_code = sis.state_code

			INNER JOIN mis_scheme.districts dt
			    ON dt.district_code = sid.district_code

			WHERE sis.scheme_id = :schemeId

			GROUP BY st.state_name, dt.district_name
			ORDER BY st.state_name, dt.district_name
			""", nativeQuery = true)
	List<Object[]> findStateWiseDistricts(@Param("schemeId") Long schemeId);

}