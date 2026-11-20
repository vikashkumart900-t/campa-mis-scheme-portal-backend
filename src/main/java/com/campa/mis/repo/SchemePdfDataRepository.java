package com.campa.mis.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SchemePdfDataRepository {

	private final JdbcTemplate jdbcTemplate;

	public Map<String, List<Map<String, Object>>> fetchAllData(Long schemeId) {

		Map<String, List<Map<String, Object>>> sections = new LinkedHashMap<>();

		// 1. Nodal Officer
		sections.put("1. Nodal Officer Details", jdbcTemplate.queryForList("""
				SELECT
				       officer_name, designation, email, mobile,
				       aadhaar_number, date_of_proposal,
				       expected_completion_date,
				       proposed_financial_outlay, scheme_duration
				FROM mis_scheme.nodal_officers
				WHERE scheme_id = ?
				ORDER BY nodal_officer_id
				""", schemeId));

		// 2. Scheme Objectives
		sections.put("2. Major Objectives", jdbcTemplate.queryForList("""
				SELECT  major_objective
				FROM mis_scheme.scheme_objectives
				WHERE scheme_id = ?
				ORDER BY display_order, objective_id
				""", schemeId));

		sections.put("2.1 Minor Objectives", jdbcTemplate.queryForList("""
				SELECT
				       so.major_objective,
				       mo.minor_objective
				FROM mis_scheme.scheme_minor_objectives mo
				JOIN mis_scheme.scheme_objectives so
				  ON so.objective_id = mo.objective_id
				WHERE so.scheme_id = ?
				ORDER BY so.display_order, mo.display_order
				""", schemeId));

		// 3. State Funding
		sections.put("3. State Funding Details", jdbcTemplate.queryForList("""
				SELECT  has_state_components, state_onboard,
				       state_agreement_file_name,
				       has_other_funding_agency,
				       other_funding_sources,
				       funding_document_file_name
				FROM mis_scheme.scheme_state_funding_details
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		// 4. Deliverables and Expenditure
		sections.put("4. Deliverable Details", jdbcTemplate.queryForList("""
				SELECT  expenditure_type,
				       national_importance,
				       international_footprints
				FROM mis_scheme.scheme_deliverable_details
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		sections.put("4.1 Scheme Deliverables", jdbcTemplate.queryForList("""
				SELECT
				       d.deliverable, d.unit,
				       d.initiation_date, d.closing_date,
				       d.financial_outlay, d.remarks,
				       d.document_file_name,
				       d.document_file_path
				FROM mis_scheme.scheme_deliverables d
				JOIN mis_scheme.scheme_deliverable_details dd
				  ON dd.id = d.deliverable_details_id
				WHERE dd.scheme_id = ?
				ORDER BY d.id
				""", schemeId));

		sections.put("4.2 Yearly Expenditures", jdbcTemplate.queryForList("""
				SELECT
				       dd.expenditure_type,
				       ye.financial_year, ye.expenditure
				FROM mis_scheme.scheme_yearly_expenditures ye
				JOIN mis_scheme.scheme_deliverable_details dd
				  ON dd.id = ye.deliverable_details_id
				WHERE dd.scheme_id = ?
				ORDER BY dd.id, ye.financial_year
				""", schemeId));

		// 5. Evaluation and Research
		sections.put("5. Evaluation and Research", jdbcTemplate.queryForList("""
				SELECT  evaluation_type, evaluation_details,
				       document_file_name,
				       research_aspects, unique_highlights
				FROM mis_scheme.scheme_evaluation_research
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		// 6. Capacity Building
		sections.put("6. Capacity Building", jdbcTemplate.queryForList("""
				SELECT  capacity_building_details
				FROM mis_scheme.scheme_capacity_building
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		// 6.1 Supporting Documents
		sections.put("6.1 Supporting Documents", jdbcTemplate.queryForList("""
				SELECT   description,
				       file_name
				FROM mis_scheme.scheme_supporting_documents
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		// 6.2 Employment Generation
		sections.put("6.2 Employment Generation", jdbcTemplate.queryForList("""
				SELECT  financial_year, labour_cost,
				       mandays_generated, tribals,
				       forest_dwellers_benefitted
				FROM mis_scheme.scheme_employment_generation
				WHERE scheme_id = ?
				ORDER BY financial_year
				""", schemeId));

		// 7. Technology Exit
		sections.put("7. Technology Exit Strategy", jdbcTemplate.queryForList("""
				SELECT
				       technological_intervention_description,
				       technological_document_name,
				       exit_protocol_description,
				       exit_protocol_document_name
				FROM mis_scheme.proposal_technology_exit
				WHERE scheme_id = ?
				ORDER BY id
				""", schemeId));

		// 8. Bank Details
		sections.put("8. Bank Details", jdbcTemplate.queryForList("""
				SELECT  account_name, bank_name,
				       ifsc_code, branch_name_address,
				       account_number
				FROM mis_scheme.bank_details
				WHERE scheme_id = ?
				ORDER BY bank_id
				""", schemeId));

		// 8.1 Installments
		sections.put("8.1 Installments", jdbcTemplate.queryForList("""
				SELECT
				       b.bank_name, i.financial_year,
				       i.installment_no, i.duration,
				       i.amount_in_lakh
				FROM mis_scheme.installments i
				JOIN mis_scheme.bank_details b
				  ON b.bank_id = i.bank_id
				WHERE b.scheme_id = ?
				ORDER BY i.financial_year, i.installment_no
				""", schemeId));

		return sections;
	}

	public String fetchSchemeName(Long schemeId) {

		return jdbcTemplate.queryForObject("""
				SELECT scheme_name
				FROM mis_scheme.schemes
				WHERE scheme_id = ?
				""", String.class, schemeId);
	}
}
