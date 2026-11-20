package com.campa.mis.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scheme_supporting_documents", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeSupportingDocumentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false)
	private Long schemeId;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "content_type")
	private String contentType;

	@Lob
	@Column(name = "file_data", columnDefinition = "BYTEA")
	private byte[] fileData;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}