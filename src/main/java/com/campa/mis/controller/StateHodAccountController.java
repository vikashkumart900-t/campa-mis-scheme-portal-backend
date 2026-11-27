package com.campa.mis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.campa.mis.dto.request.BulkStateHodEmailRequest;
import com.campa.mis.dto.request.StateHodRegistrationRequest;
import com.campa.mis.dto.response.BulkStateHodEmailResponse;
import com.campa.mis.dto.response.StateHodRegistrationResponse;
import com.campa.mis.service.StateHodAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
@Validated
public class StateHodAccountController {

	private final StateHodAccountService stateHodAccountService;

	// REGISTER STATE HOD
	@PostMapping("/register-stateHod")
	public ResponseEntity<StateHodRegistrationResponse> registerStateHod(
			@Valid @RequestBody StateHodRegistrationRequest request) {

		StateHodRegistrationResponse response = this.stateHodAccountService.registerStateHod(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// BULK SEND EMAIL TO SELECTED STATES
	@PostMapping("/bulk-send-email")
	public ResponseEntity<BulkStateHodEmailResponse> bulkSendEmail(
			@Valid @RequestBody BulkStateHodEmailRequest request) {

		BulkStateHodEmailResponse response = this.stateHodAccountService.bulkSendEmail(request);

		return ResponseEntity.ok(response);
	}

	// GET STATE HOD STATUS SCHEME-WISE
	@GetMapping("/hod-status/{schemeId}")
	public ResponseEntity<List<StateHodRegistrationResponse>> getStateHodStatus(@PathVariable Long schemeId) {

		List<StateHodRegistrationResponse> response = stateHodAccountService.getStateHodStatus(schemeId);

		return ResponseEntity.ok(response);
	}
}