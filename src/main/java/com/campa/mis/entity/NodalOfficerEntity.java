package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "nodal_officers", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodalOfficerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nodal_officer_id")
	private Long nodalOfficerId;

	@Column(name = "organization_id", nullable = false)
	private Long organizationId;

	@Column(name = "officer_name", nullable = false, length = 150)
	private String officerName;

	@Column(name = "designation", length = 150)
	private String designation;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "mobile", nullable = false, length = 15)
	private String mobile;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "aadhaar_number", length = 12)
	private String aadhaarNumber;

	@Column(name = "date_of_proposal")
	private LocalDate dateOfProposal;

	@Column(name = "expected_completion_date")
	private LocalDate expectedCompletionDate;

	@Column(name = "proposed_financial_outlay", precision = 15, scale = 2)
	private BigDecimal proposedFinancialOutlay;

	@Column(name = "scheme_duration", length = 100)
	private String schemeDuration;
	
	@Column(name="org_name")
	private String orgName;

	@PrePersist
	protected void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();

		if (createdAt == null) {
			createdAt = now;
		}

	}

}