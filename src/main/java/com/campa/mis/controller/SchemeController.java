package com.campa.mis.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campa.mis.dto.request.ForgotPasswordRequest;
import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.request.ResetPasswordRequest;
import com.campa.mis.dto.request.VerifyOtpRequest;
import com.campa.mis.dto.response.ApiResponse;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.PasswordResetResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.SchemeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/campa")
public class SchemeController {

	private final SchemeService schemeService;
	private final JwtService jwtService;

	public SchemeController(SchemeService schemeService, JwtService jwtService) {
		this.schemeService = schemeService;
		this.jwtService = jwtService;
	}

	// Dropdown data
	@GetMapping("/dropdown-metadata")
	public ResponseEntity<Map<String, Object>> getDropdownData(@RequestParam(required = false) Long organizationId) {

		return ResponseEntity.ok(this.schemeService.getDropdownData(organizationId));
	}

	// Districts by state
	@GetMapping("/districts/{stateCode}")
	public ResponseEntity<List<DistrictEntity>> getDistrictsByStateId(@PathVariable String stateCode) {

		return ResponseEntity.ok(this.schemeService.getDistrictsByStateCode(stateCode));
	}

	// get all state
	@GetMapping("/states")
	public ResponseEntity<List<StateEntity>> getAllStates() {

		return ResponseEntity.ok(this.schemeService.getAllStates());
	}

	// get all schemes
	@GetMapping("/active-schemes")
	public ResponseEntity<List<SchemeEntity>> getAllActiveSchemes() {
		return ResponseEntity.ok(this.schemeService.getAllActiveSchemes());
	}

	@PostMapping("/parivesh/login") // only for dfo,state_hod and campa user
	public ResponseEntity<String> pariveshLogin(@RequestBody LoginRequestDTO request) {

		return this.schemeService.pariveshLogin(request);
	}

	@PostMapping("/mis/login") // only for IA,PD and HOD central user
	public ResponseEntity<LoginResponse> misLogin(@RequestBody LoginRequestDTO request) {

		return ResponseEntity.ok(this.schemeService.misLogin(request));
	}

	@GetMapping("/{schemeId}/details")
	public ResponseEntity<SchemeDetailsResponse> getSchemeDetails(@PathVariable Long schemeId) {

		return this.schemeService.getSchemeDetailsById(schemeId).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/getAllRoles")
	public ResponseEntity<List<UserRoleEntity>> getAllRoles() {
		return ResponseEntity.ok(this.schemeService.getAllRoles());
	}

	@GetMapping("/program-division-organizations")
	public List<ProgramOrganizationsResponseDTO> getAllDivisionOrganizations() {
		return this.schemeService.getAllDivisionOrganizations();
	}

	@PostMapping("/register")
	public ResponseEntity<UserAccountEntity> register(@Valid @RequestBody RegistrationRequestDTO request) {

		UserAccountEntity account = this.schemeService.register(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(account);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

		this.schemeService.forgotPassword(request.email());

		return ResponseEntity
				.ok(ApiResponse.success("If the account exists, password reset instructions have been sent."));
	}

	@PostMapping("/verify-reset-otp")
	public ResponseEntity<PasswordResetResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

		String resetToken = this.schemeService.verifyOtp(request.email(), request.otp());

		return ResponseEntity.ok(new PasswordResetResponse(true, "OTP verified successfully", resetToken));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

		this.schemeService.resetPassword(request);

		return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
	}

}