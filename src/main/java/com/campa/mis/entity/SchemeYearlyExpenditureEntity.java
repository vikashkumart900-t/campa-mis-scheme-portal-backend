package com.campa.mis.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_yearly_expenditures", schema = "mis_scheme")
@Getter
@Setter
public class SchemeYearlyExpenditureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "deliverable_details_id", nullable = false)
	private Long deliverableDetailsId;

	@Column(name = "financial_year", nullable = false)
	private String financialYear;

	@Column(name = "expenditure", nullable = false)
	private BigDecimal expenditure;

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