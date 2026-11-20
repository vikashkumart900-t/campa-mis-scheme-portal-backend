package com.campa.mis.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bank_details", schema = "mis_scheme")
@Getter
@Setter
public class BankDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bank_id")
	private Integer bankId;

	@Column(name = "scheme_id", nullable = false)
	private Long schemeId;

	@Column(name = "account_name", nullable = false, length = 150)
	private String accountName;

	@Column(name = "bank_name", nullable = false, length = 150)
	private String bankName;

	@Column(name = "ifsc_code", nullable = false, length = 11)
	private String ifscCode;

	@Column(name = "branch_name_address", nullable = false)
	private String branchNameAddress;

	@Column(name = "account_number", nullable = false, length = 30)
	private String accountNumber;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "bankDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InstallmentEntity> installments = new ArrayList<>();

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addInstallment(InstallmentEntity installment) {
		installments.add(installment);
		installment.setBankDetails(this);
	}
}