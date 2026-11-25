package com.campa.mis.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_deliverables", schema = "mis_scheme")
@Getter
@Setter
public class SchemeDeliverablesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "deliverable_details_id", nullable = false)
	private Long deliverableDetailsId;

	@Column(name = "deliverable", nullable = false)
	private String deliverable;

	@Column(name = "unit", nullable = false)
	private String unit;

	@Column(name = "initiation_date", nullable = false)
	private LocalDate initiationDate;

	@Column(name = "closing_date", nullable = false)
	private LocalDate closingDate;

	@Column(name = "financial_outlay", nullable = false)
	private BigDecimal financialOutlay;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "document_file_name")
	private String documentFileName;

	@Column(name = "document_file_data")
	@JdbcTypeCode(SqlTypes.VARBINARY)
	private byte[] documentFileData;

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