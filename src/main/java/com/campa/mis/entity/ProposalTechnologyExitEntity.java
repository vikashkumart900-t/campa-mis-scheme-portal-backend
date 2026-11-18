package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_technology_exit", schema = "mis_scheme")
@Getter
@Setter
public class ProposalTechnologyExitEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false)
	private Long SchemeId;

	// Section 13
	@Column(name = "technological_intervention_description", nullable = false, columnDefinition = "TEXT")
	private String technologicalInterventionDescription;

	@Column(name = "technological_document_name")
	private String technologicalDocumentName;

	@Column(name = "technological_document_type")
	private String technologicalDocumentType;

	@Column(name = "technological_document_data", columnDefinition = "BYTEA")
	private byte[] technologicalDocumentData;

	// Section 14
	@Column(name = "exit_protocol_description", nullable = false, columnDefinition = "TEXT")
	private String exitProtocolDescription;

	@Column(name = "exit_protocol_document_name")
	private String exitProtocolDocumentName;

	@Column(name = "exit_protocol_document_type")
	private String exitProtocolDocumentType;

	@Column(name = "exit_protocol_document_data", columnDefinition = "BYTEA")
	private byte[] exitProtocolDocumentData;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}