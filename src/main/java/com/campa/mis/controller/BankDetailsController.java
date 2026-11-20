package com.campa.mis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	// GET BY ID
	@GetMapping("/{bankId}")
	public ResponseEntity<BankDetailsResponse> getBankDetails(@PathVariable Integer bankId) {

		return ResponseEntity.ok(bankDetailsService.getBankDetails(bankId));
	}

	// GET ALL
	@GetMapping
	public ResponseEntity<List<BankDetailsResponse>> getAllBankDetails() {

		return ResponseEntity.ok(bankDetailsService.getAllBankDetails());
	}

	// UPDATE
	@PutMapping("/{bankId}")
	public ResponseEntity<BankDetailsResponse> updateBankDetails(@PathVariable Integer bankId,
			@Valid @RequestBody BankDetailsRequest request) {

		return ResponseEntity.ok(bankDetailsService.updateBankDetails(bankId, request));
	}

}