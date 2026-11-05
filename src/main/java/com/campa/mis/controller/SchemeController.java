package com.campa.mis.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.response.ApiResponse;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.SchemeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/campa")
@CrossOrigin(origins = "*")
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

	// Register nodal officer
	@PostMapping("/nodal-officer")
	public ResponseEntity<String> saveNodalOfficerDtls(@RequestBody NodalOfficerRegistrationRequestDTO request) {

		return ResponseEntity.ok(this.schemeService.saveNodalOfficerData(request));
	}

	// get all active schemes
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

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication, HttpServletRequest request) {

		// Validate authentication
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
		}

		// Get authenticated user's email
		String email = authentication.getName();

		// Extract JWT from Authorization header
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error("Invalid or missing Authorization header"));
		}

		String jwt = authorization.substring(7);

		// Extract token ID
		String tokenId;

		try {
			tokenId = jwtService.extractTokenId(jwt);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.error("Invalid or expired access token"));
		}

		// Invalidate user session
		schemeService.logout(email, tokenId);

		return ResponseEntity.ok(ApiResponse.success("Logout successful"));
	}

}