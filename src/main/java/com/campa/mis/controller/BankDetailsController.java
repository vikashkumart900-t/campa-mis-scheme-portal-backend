package com.campa.mis.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campa.mis.dto.request.BankDetailsRequest;
import com.campa.mis.dto.response.BankDetailsResponse;
import com.campa.mis.service.BankDetailsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class BankDetailsController {

	private final BankDetailsService bankDetailsService;

	// CREATE
	@PostMapping("/{schemeId}/bank-details")
	public ResponseEntity<BankDetailsResponse> saveBankDetails(@PathVariable Long schemeId,
			@Valid @RequestBody BankDetailsRequest request) {

		return ResponseEntity.ok(bankDetailsService.saveBankDetails(request, schemeId));
	}

	// GET ALL
	@GetMapping
	public ResponseEntity<List<BankDetailsResponse>> getAllBankDetails() {

		return ResponseEntity.ok(bankDetailsService.getAllBankDetails());
	}

	// FETCH BANK DETAILS BY SCHEME ID
	@GetMapping("/bank-details/{schemeId}")
	public ResponseEntity<BankDetailsResponse> getBankDetails(@PathVariable Long schemeId) {

		return ResponseEntity.ok(bankDetailsService.getBankDetails(schemeId));
	}

}