package com.campa.mis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.response.ApiResponse;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.SchemeService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/mis")
public class SchemeImplementationController {

	private final SchemeService schemeService;
	private final JwtService jwtService;

	public SchemeImplementationController(SchemeService schemeService, JwtService jwtService) {
		this.schemeService = schemeService;
		this.jwtService = jwtService;
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
			tokenId = this.jwtService.extractTokenId(jwt);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.error("Invalid or expired access token"));
		}

		// Invalidate user session
		this.schemeService.logout(email, tokenId);

		return ResponseEntity.ok(ApiResponse.success("Logout successful"));
	}

	// Register nodal officer
	@PostMapping("/nodal-officer")
	public ResponseEntity<String> saveNodalOfficerDtls(@RequestBody NodalOfficerRegistrationRequestDTO request) {

		return ResponseEntity.ok(this.schemeService.saveNodalOfficerData(request));
	}

}
