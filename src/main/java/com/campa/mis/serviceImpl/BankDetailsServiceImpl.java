package com.campa.mis.serviceImpl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campa.mis.dto.request.BankDetailsRequest;
import com.campa.mis.dto.request.InstallmentRequest;
import com.campa.mis.dto.response.BankDetailsResponse;
import com.campa.mis.entity.BankDetailsEntity;
import com.campa.mis.entity.InstallmentEntity;
import com.campa.mis.repo.BankDetailRepository;
import com.campa.mis.service.BankDetailsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankDetailsServiceImpl implements BankDetailsService {

	private final BankDetailRepository bankDetailsRepository;

	// CREATE
	@Transactional
	public BankDetailsResponse saveBankDetails(BankDetailsRequest request, Long schemeId) {

		BankDetailsEntity bankDetailsEntity = bankDetailsRepository.findBySchemeId(schemeId)
				.orElseGet(BankDetailsEntity::new);

		bankDetailsEntity.setSchemeId(schemeId);
		bankDetailsEntity.setAccountName(request.accountName().trim());
		bankDetailsEntity.setBankName(request.bankName().trim());
		bankDetailsEntity.setIfscCode(request.ifscCode().trim().toUpperCase());
		bankDetailsEntity.setBranchNameAddress(request.branchNameAddress().trim());
		bankDetailsEntity.setAccountNumber(request.accountNumber().trim());

		// Existing installments remove
		if (bankDetailsRepository.existsBySchemeId(schemeId)) {
			bankDetailsEntity.getInstallments().clear();
		}

		for (InstallmentRequest installmentRequest : request.installments()) {

			InstallmentEntity installment = new InstallmentEntity();

			installment.setFinancialYear(installmentRequest.financialYear());
			installment.setInstallmentNo(installmentRequest.installmentNo());
			installment.setDuration(installmentRequest.duration());
			installment.setAmountInLakh(installmentRequest.amountInLakh());

			bankDetailsEntity.addInstallment(installment);
		}

		BankDetailsEntity savedBank = bankDetailsRepository.save(bankDetailsEntity);

		return mapToResponse(savedBank);
	}

	// GET BY ID
	@Transactional(readOnly = true)
	public BankDetailsResponse getBankDetails(Integer bankId) {

		BankDetailsEntity bank = bankDetailsRepository.findById(bankId)
				.orElseThrow(() -> new RuntimeException("Bank details not found for ID: " + bankId));

		return mapToResponse(bank);
	}

	// GET ALL
	@Transactional(readOnly = true)
	public List<BankDetailsResponse> getAllBankDetails() {

		return bankDetailsRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	// UPDATE
	@Transactional
	public BankDetailsResponse updateBankDetails(Integer bankId, BankDetailsRequest request) {

		BankDetailsEntity bank = bankDetailsRepository.findById(bankId)
				.orElseThrow(() -> new RuntimeException("Bank details not found for ID: " + bankId));

		bank.setAccountName(request.accountName().trim());
		bank.setBankName(request.bankName().trim());
		bank.setIfscCode(request.ifscCode().trim().toUpperCase());
		bank.setBranchNameAddress(request.branchNameAddress().trim());
		bank.setAccountNumber(request.accountNumber().trim());

		// Remove existing installments
		bank.getInstallments().clear();

		// Insert updated installments
		for (InstallmentRequest installmentRequest : request.installments()) {

			InstallmentEntity installment = new InstallmentEntity();

			installment.setFinancialYear(installmentRequest.financialYear());
			installment.setInstallmentNo(installmentRequest.installmentNo());
			installment.setDuration(installmentRequest.duration());
			installment.setAmountInLakh(installmentRequest.amountInLakh());

			bank.addInstallment(installment);
		}

		return mapToResponse(bank);
	}

	// ENTITY TO RESPONSE MAPPING
	private BankDetailsResponse mapToResponse(BankDetailsEntity bank) {

		List<BankDetailsResponse.InstallmentResponse> installmentResponses = bank.getInstallments().stream()
				.map(installment -> new BankDetailsResponse.InstallmentResponse(installment.getInstallmentId(),
						installment.getFinancialYear(), installment.getInstallmentNo(), installment.getDuration(),
						installment.getAmountInLakh(), installment.getCreatedAt()))
				.toList();

		return new BankDetailsResponse(bank.getBankId(), bank.getAccountName(), bank.getBankName(), bank.getIfscCode(),
				bank.getBranchNameAddress(), bank.getAccountNumber(), bank.getCreatedAt(), bank.getUpdatedAt(),
				installmentResponses);
	}
}