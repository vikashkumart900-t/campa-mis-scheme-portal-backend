
package com.campa.mis.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.campa.mis.enums.EvaluationType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_evaluation_research", schema = "mis_scheme")
@Getter
@Setter
public class SchemeEvaluationResearchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false)
	private Long schemeId;

	// ANNUAL or MIDTERM
	@Enumerated(EnumType.STRING)
	@Column(name = "evaluation_type", nullable = false, length = 20)
	private EvaluationType evaluationType;

	// Multiple evaluation text areas stored as JSONB
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "evaluation_details", nullable = false, columnDefinition = "jsonb")
	private List<String> evaluationDetails;

	// Supporting document details
	@Column(name = "document_file_name", length = 255)
	private String documentFileName;

	@Column(name = "document_content_type", length = 100)
	private String documentContentType;

	// Document stored in PostgreSQL BYTEA
	@JdbcTypeCode(SqlTypes.VARBINARY)
	@Column(name = "supporting_document", columnDefinition = "BYTEA")
	private byte[] supportingDocument;

	// 8.1 Research Aspects
	@Column(name = "research_aspects", nullable = false, columnDefinition = "TEXT")
	private String researchAspects;

	// 8.2 Unique Highlights
	@Column(name = "unique_highlights", columnDefinition = "TEXT")
	private String uniqueHighlights;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
