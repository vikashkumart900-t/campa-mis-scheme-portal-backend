package com.campa.mis.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "installments", schema = "mis_scheme")
@Getter
@Setter
public class InstallmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "installment_id")
	private Integer installmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id", nullable = false)
	private BankDetailsEntity bankDetails;

	@Column(name = "financial_year", nullable = false, length = 10)
	private String financialYear;

	@Column(name = "installment_no", nullable = false)
	private Integer installmentNo;

	@Column(name = "duration", nullable = false, length = 50)
	private String duration;

	@Column(name = "amount_in_lakh", nullable = false, precision = 12, scale = 2)
	private BigDecimal amountInLakh;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}
}