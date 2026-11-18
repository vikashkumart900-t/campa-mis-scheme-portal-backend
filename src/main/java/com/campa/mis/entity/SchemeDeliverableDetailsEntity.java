package com.campa.mis.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_deliverable_details", schema = "mis_scheme")
@Getter
@Setter
public class SchemeDeliverableDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false, unique = true)
	private Long schemeId;

	@Column(name = "expenditure_type", nullable = false)
	private String expenditureType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "national_importance", columnDefinition = "jsonb")
	private List<String> nationalImportance;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "international_footprints", columnDefinition = "jsonb")
	private List<String> internationalFootprints;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

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