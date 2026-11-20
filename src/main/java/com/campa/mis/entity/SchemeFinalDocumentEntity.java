package com.campa.mis.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "scheme_final_documents", schema = "mis_scheme")
@Data
public class SchemeFinalDocumentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "document_id")
	private Long documentId;

	@Column(name = "scheme_id", nullable = false, unique = true)
	private Long schemeId;

	@Column(name = "signature_file_name")
	private String signatureFileName;

	@Column(name = "signature_file_data")
	private byte[] signatureFileData;

	@Column(name = "supporting_document_name")
	private String supportingDocumentName;

	@Column(name = "supporting_document_data")
	private byte[] supportingDocumentData;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt;

	@PrePersist
	@PreUpdate
	public void updateTimestamp() {
		this.uploadedAt = LocalDateTime.now();
	}
}