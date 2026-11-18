package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "scheme_state_funding_details", schema = "mis_scheme")
@Getter
@Setter
public class SchemeStateFundingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false, unique = true)
	private Long schemeId;

	@Column(name = "has_state_components", nullable = false)
	private Boolean hasStateComponents;

	@Column(name = "state_onboard")
	private Boolean stateOnboard;

	@Column(name = "state_agreement_file_name")
	private String stateAgreementFileName;

	@Column(name = "has_other_funding_agency", nullable = false)
	private Boolean hasOtherFundingAgency;

	@Column(name = "other_funding_sources")
	private String otherFundingSources;

	@Column(name = "funding_document_file_name")
	private String fundingDocumentFileName;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@JdbcTypeCode(SqlTypes.VARBINARY)
	@Column(name = "state_agreement_file", columnDefinition = "bytea")
	private byte[] stateAgreementFile;

	@JdbcTypeCode(SqlTypes.VARBINARY)
	@Column(name = "funding_document_file", columnDefinition = "bytea")
	private byte[] fundingDocumentFile;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}