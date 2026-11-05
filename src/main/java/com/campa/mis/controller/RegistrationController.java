package com.campa.mis.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.RegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/campa")
@CrossOrigin(origins = "*")
public class RegistrationController {

	private final RegistrationService registrationService;
	private final JwtService jwtService;

	public RegistrationController(RegistrationService registrationService, JwtService jwtService) {
		this.registrationService = registrationService;
		this.jwtService = jwtService;
	}

	// Dropdown data
	@GetMapping("/dropdown-metadata")
	public ResponseEntity<Map<String, Object>> getDropdownData(@RequestParam(required = false) Long organizationId) {

		return ResponseEntity.ok(registrationService.getDropdownData(organizationId));
	}

	// Districts by state
	@GetMapping("/districts/{stateCode}")
	public ResponseEntity<List<DistrictEntity>> getDistrictsByStateId(@PathVariable String stateCode) {

		return ResponseEntity.ok(registrationService.getDistrictsByStateCode(stateCode));
	}

	// get all state
	@GetMapping("/states")
	public ResponseEntity<List<StateEntity>> getAllStates() {

		return ResponseEntity.ok(registrationService.getAllStates());
	}

	// Register nodal officer
	@PostMapping("/nodal-officer")
	public ResponseEntity<String> registerNodalOfficer(@RequestBody NodalOfficerRegistrationRequestDTO request) {

		return ResponseEntity.ok(registrationService.registerNodalOfficer(request));
	}

	// get all active schemes
	@GetMapping("/active-schemes")
	public ResponseEntity<List<SchemeEntity>> getAllActiveSchemes() {
		return ResponseEntity.ok(registrationService.getAllActiveSchemes());
	}

	@PostMapping("/parivesh/login") // only for dfo,state_hod and campa user
	public ResponseEntity<String> pariveshLogin(@RequestBody LoginRequestDTO request) {

		return this.registrationService.pariveshLogin(request);
	}

	@PostMapping("/mis/login") // only for IA,PD and HOD central user
	public ResponseEntity<LoginResponse> misLogin(@RequestBody LoginRequestDTO request) {

		return ResponseEntity.ok(registrationService.misLogin(request));
	}

	@GetMapping("/{schemeId}/details")
	public ResponseEntity<SchemeDetailsResponse> getSchemeDetails(@PathVariable Long schemeId) {

		return registrationService.getSchemeDetailsById(schemeId).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/getAllRoles")
	public ResponseEntity<List<UserRoleEntity>> getAllRoles() {
		return ResponseEntity.ok(registrationService.getAllRoles());
	}

	@GetMapping("/program-division-organizations")
	public List<ProgramOrganizationsResponseDTO> getAllDivisionOrganizations() {
		return registrationService.getAllDivisionOrganizations();
	}

	@PostMapping("/register")
	public ResponseEntity<UserAccountEntity> register(@Valid @RequestBody RegistrationRequestDTO request) {

		UserAccountEntity account = this.registrationService.register(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(account);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(
	        Authentication authentication,
	        HttpServletRequest request) {

	    String email = authentication.getName();

	    String authorization =
	            request.getHeader("Authorization");

	    String jwt = authorization.substring(7);

	    String tokenId = jwtService.extractTokenId(jwt);

	    this.registrationService.logout(email, tokenId);

	    return ResponseEntity.ok(
	        Map.of(
	            "success", true,
	            "message", "Logout successful"
	        )
	    );
	}

}