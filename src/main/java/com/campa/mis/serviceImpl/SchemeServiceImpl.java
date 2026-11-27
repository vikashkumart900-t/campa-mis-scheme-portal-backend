package com.campa.mis.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.ProposalListingProjectionResponse;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.request.ResetPasswordRequest;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.NodalOfficerDetailsDTO;
import com.campa.mis.dto.response.NodalOfficerDetailsResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.dto.response.StateDistrictResponse;
import com.campa.mis.dto.response.UserAccountProjection;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.NodalOfficerEntity;
import com.campa.mis.entity.OrganizationEntity;
import com.campa.mis.entity.PasswordResetRequestEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.SchemeImplementationDistrictEntity;
import com.campa.mis.entity.SchemeImplementationStateEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.repo.DistrictRepository;
import com.campa.mis.repo.NodalOfficerRepository;
import com.campa.mis.repo.OrganizationRepository;
import com.campa.mis.repo.PasswordResetRequestRepository;
import com.campa.mis.repo.ProgramDivisionRepository;
import com.campa.mis.repo.SchemeImplementationDistrictRepository;
import com.campa.mis.repo.SchemeImplementationStateRepository;
import com.campa.mis.repo.SchemeRepository;
import com.campa.mis.repo.StateRepository;
import com.campa.mis.repo.SubAgencyRepository;
import com.campa.mis.repo.UserAccountRepository;
import com.campa.mis.repo.UserRoleRepository;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.EmailService;
import com.campa.mis.service.SchemeService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@RequiredArgsConstructor
public class SchemeServiceImpl implements SchemeService {

	private final RestTemplate restTemplate;
	private final PasswordEncoder passwordEncoder;
	private final ObjectMapper objectMapper;

	@Value("${parivesh.base-url}")
	private String baseUrl;

	@Value("${parivesh.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration-ms}")
	private long expirationTime;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final SchemeImplementationStateRepository schemeImplementationStateRepository;
	private final SchemeImplementationDistrictRepository schemeImplementationDistrictRepository;
	private final OrganizationRepository organizationRepository;
	private final StateRepository stateRepository;
	private final DistrictRepository districtRepository;
	private final SubAgencyRepository subAgencyRepository;
	private final ProgramDivisionRepository programDivisionRepository;
	private final NodalOfficerRepository nodalOfficerRepository;
	// private final BCryptPasswordEncoder passwordEncoder;
	private final SchemeRepository schemeRepository;
	private final UserRoleRepository userRoleRepository;
	private final UserAccountRepository userAccountRepository;
	private final JwtService jwtService;
	private final PasswordResetRequestRepository passwordResetRepository;
	private final EmailService emailService;

	// 1. Organizations, states, program divisions and sub-agencies
	@Override
	public Map<String, Object> getDropdownData(Long organizationId) {

		Map<String, Object> data = new HashMap<>();

		data.put("organizations", organizationRepository.findAll());
		data.put("states", stateRepository.findAll());
		data.put("programDivisions", programDivisionRepository.findAll());

		if (organizationId != null) {

			OrganizationEntity organization = organizationRepository.findById(organizationId)
					.orElseThrow(() -> new IllegalArgumentException("Organization not found"));

			if ("SUB_IA".equalsIgnoreCase(organization.getIaCategory())) {

				data.put("subAgencies", subAgencyRepository.findByOrganizationId(organizationId));
			}
		}

		return data;
	}

	// 2. Districts by state
	@Override
	public List<DistrictEntity> getDistrictsByStateCode(String stateCode) {

		return districtRepository.findByStateCode(stateCode);
	}

	// 3. Nodal officer registration
	@Override
	@Transactional
	public NodalOfficerRegistrationRequestDTO saveNodalOfficerData(NodalOfficerRegistrationRequestDTO request) {

		if (request == null) {
			throw new IllegalArgumentException("Registration request is required");
		}

		// 1. Validate mandatory fields

		if (request.organizationId() == null) {
			throw new IllegalArgumentException("Organization ID is required");
		}

		if (request.schemeId() == null) {
			throw new IllegalArgumentException("Scheme ID is required");
		}

		if (request.stateDistricts() == null || request.stateDistricts().isEmpty()) {
			throw new IllegalArgumentException("At least one state is required");
		}

		if (request.officerName() == null || request.officerName().isBlank()) {
			throw new IllegalArgumentException("Officer name is required");
		}

		if (request.designation() == null || request.designation().isBlank()) {
			throw new IllegalArgumentException("Designation is required");
		}

		if (request.email() == null || request.email().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (request.mobile() == null || request.mobile().isBlank()) {
			throw new IllegalArgumentException("Mobile number is required");
		}

		// 2. Save nodal officer

		NodalOfficerEntity officerEntity = this.nodalOfficerRepository.findBySchemeId(request.schemeId())
				.orElseGet(NodalOfficerEntity::new);

		officerEntity.setOfficerName(request.officerName().trim());
		officerEntity.setDesignation(request.designation().trim());
		officerEntity.setEmail(request.email().trim());
		officerEntity.setMobile(request.mobile().trim());
		officerEntity.setAadhaarNumber(request.aadhaarNumber());
		officerEntity.setCreatedAt(OffsetDateTime.now());
		officerEntity.setExpectedCompletionDate(request.expectedCompletionDate());
		officerEntity.setDateOfProposal(request.dateOfProposal());
		officerEntity.setProposedFinancialOutlay(request.proposedFinancialOutlay());
		officerEntity.setSchemeDuration(request.schemeDuration());
		officerEntity.setDivisionId(request.divisionId());
		officerEntity.setSchemeId(request.schemeId());
		officerEntity.setOrganizationId(request.organizationId());

		this.nodalOfficerRepository.save(officerEntity);

		Long schemeId = request.schemeId();

		// 1. Fetch existing state mappings
		List<SchemeImplementationStateEntity> existingStates = schemeImplementationStateRepository
				.findBySchemeId(schemeId);

		// 2. Track incoming state codes
		Set<String> selectedStateCodes = new HashSet<>();

		// 3. Process states received from frontend
		for (NodalOfficerRegistrationRequestDTO.StateDistrictRequestDTO stateRequest : request.stateDistricts()) {

			String stateCode = stateRequest.stateCode().trim();

			if (!selectedStateCodes.add(stateCode)) {
				throw new IllegalArgumentException("Duplicate state code: " + stateCode);
			}

			// Validate state
			stateRepository.findByStateCode(stateCode)
					.orElseThrow(() -> new IllegalArgumentException("Invalid state code: " + stateCode));

			// Get distinct district codes
			Set<String> incomingDistrictCodes = stateRequest.districtCodes() == null ? Set.of()
					: stateRequest.districtCodes().stream().map(String::trim).collect(Collectors.toSet());

			// 4. Find existing state mapping, otherwise insert new
			SchemeImplementationStateEntity stateEntity = schemeImplementationStateRepository
					.findBySchemeIdAndStateCode(schemeId, stateCode).orElseGet(() -> {
						SchemeImplementationStateEntity entity = new SchemeImplementationStateEntity();
						entity.setSchemeId(schemeId);
						entity.setStateCode(stateCode);

						return schemeImplementationStateRepository.save(entity);
					});

			Long stateMappingId = stateEntity.getId();

			// 5. Fetch existing district mappings
			List<SchemeImplementationDistrictEntity> existingDistricts = schemeImplementationDistrictRepository
					.findBySchemeImplementationStateId(stateMappingId);

			// 6. Identify existing district codes
			Set<String> existingDistrictCodes = existingDistricts.stream()
					.map(SchemeImplementationDistrictEntity::getDistrictCode).collect(Collectors.toSet());

			// 7. Delete only districts removed by user
			List<Long> removedDistrictIds = existingDistricts.stream()
					.filter(d -> !incomingDistrictCodes.contains(d.getDistrictCode()))
					.map(SchemeImplementationDistrictEntity::getId).toList();

			if (!removedDistrictIds.isEmpty()) {
				schemeImplementationDistrictRepository.deleteAllByIdInBatch(removedDistrictIds);
			}

			// 8. Insert only newly selected districts
			for (String districtCode : incomingDistrictCodes) {

				if (!existingDistrictCodes.contains(districtCode)) {

					SchemeImplementationDistrictEntity districtEntity = new SchemeImplementationDistrictEntity();

					districtEntity.setSchemeImplementationStateId(stateMappingId);
					districtEntity.setDistrictCode(districtCode);

					schemeImplementationDistrictRepository.save(districtEntity);
				}
			}
		}

		// 9. Identify states removed by user
		List<Long> removedStateIds = existingStates.stream().filter(s -> !selectedStateCodes.contains(s.getStateCode()))
				.map(SchemeImplementationStateEntity::getId).toList();

		// 10. Delete child districts first, then parent states
		if (!removedStateIds.isEmpty()) {

			schemeImplementationDistrictRepository.deleteBySchemeImplementationStateIdIn(removedStateIds);

			schemeImplementationDistrictRepository.flush();

			schemeImplementationStateRepository.deleteAllByIdInBatch(removedStateIds);
		}

		return request;
	}

	@Override
	public List<SchemeEntity> getAllActiveSchemes() {

		return schemeRepository.findAll();

	}

//	@Override
//	public ResponseEntity<String> pariveshLogin(LoginRequestDTO request) {
//
//		String url = UriComponentsBuilder.fromUriString(baseUrl).path("/api/integration/login")
//				.queryParam("secretKey", secretKey).build().encode().toUriString();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
//
//		HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);
//
//		try {
//			return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//
//		} catch (RestClientResponseException ex) {
//			// Forward the external API's HTTP status and response body.
//			return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
//					.body(ex.getResponseBodyAsString());
//
//		} catch (RestClientException ex) {
//			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
//					.body("{\"message\":\"Unable to connect to PARIVESH service\"}");
//		}
//	}

	@Override
	@Transactional
	public ResponseEntity<String> pariveshLogin(LoginRequestDTO request) {

		String url = UriComponentsBuilder.fromUriString(baseUrl).path("/api/integration/login")
				.queryParam("secretKey", secretKey).build().encode().toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			// Parse external response
			ObjectNode root = (ObjectNode) objectMapper.readTree(response.getBody());

			// Get CAMPA user
			UserAccountProjection user = userAccountRepository.findUserForAuthentication(request.username())
					.orElseThrow(() -> new BadCredentialsException("CAMPA user not found"));

			// Check account status
			if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
				throw new DisabledException("Your account is not active.");
			}

			// Generate CAMPA JWT
			String accessToken = generateCampaAccessToken(user);

			// Override only data.jwt
			ObjectNode data = (ObjectNode) root.get("data");
			data.put("jwt", accessToken);

			return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
					.body(objectMapper.writeValueAsString(root));

		} catch (RestClientResponseException ex) {

			return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
					.body(ex.getResponseBodyAsString());

		} catch (RestClientException ex) {

			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("{\"message\":\"Unable to connect to PARIVESH service\"}");

		} catch (Exception ex) {

			throw new RuntimeException("Failed to process PARIVESH login response: " + ex.getMessage(), ex);
		}
	}

	private String generateCampaAccessToken(UserAccountProjection user) {

		String email = user.getEmail();
		Long userId = user.getUserId();
		String roleCode = user.getRoleCode();

		String tokenId = UUID.randomUUID().toString();

		UserDetails userDetails = User.withUsername(email).password(user.getPasswordHash())
				.authorities("ROLE_" + roleCode).build();

		String accessToken = jwtService.generateToken(userDetails, tokenId);

		Instant expiresAt = Instant.now().plusMillis(expirationTime);

		int updated = userAccountRepository.updateActiveSession(userId, tokenId, expiresAt.atOffset(ZoneOffset.UTC));

		if (updated != 1) {
			throw new IllegalStateException("Unable to create login session.");
		}

		return accessToken;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SchemeDetailsResponse> getSchemeDetailsById(Long schemeId) {

		return schemeRepository.findSchemeDetailsById(schemeId).map(result -> {

			// Handle nested Object[] result
			Object[] row = result;

			if (row.length == 1 && row[0] instanceof Object[]) {
				row = (Object[]) row[0];
			}

			String schemeName = row[0] != null ? row[0].toString() : null;

			String organizationName = row[1] != null ? row[1].toString() : null;

			String divisionName = row[2] != null ? row[2].toString() : null;

			Long organizationId = row[3] != null ? Long.valueOf(row[3].toString()) : null;

			Long divisionId = row[4] != null ? Long.valueOf(row[4].toString()) : null;

			Long schemeIdValue = row[5] != null ? Long.valueOf(row[5].toString()) : null;

			return new SchemeDetailsResponse(schemeName, organizationName, divisionName, organizationId, divisionId,
					schemeIdValue);
		});
	}

	@Override
	public List<UserRoleEntity> getAllRoles() {

		return userRoleRepository.findAll();
	}

	@Override
	public List<ProgramOrganizationsResponseDTO> getAllDivisionOrganizations() {
		return programDivisionRepository.getAllDivisionOrganizations();
	}

	@Override
	@Transactional
	public UserAccountEntity register(RegistrationRequestDTO request) {

		if (request.fullName() == null || request.email() == null || request.mobile() == null
				|| request.password() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email, mobile and password are required");
		}

		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (userAccountRepository.existsByEmailIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
		}

		// Resolve role from the database, not from a trusted client-supplied ID.
		String roleCode = request.roleCode();

		if (roleCode == null || roleCode.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
		}

		if (!roleCode.equals("IA") && !roleCode.equals("PD") && !roleCode.equals("HOD")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only IA ,HOD and PD registration is supported");
		}

		var role = userRoleRepository.findByRoleCode(roleCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleCode));

		if (roleCode.equalsIgnoreCase("IA") || roleCode.equalsIgnoreCase("HOD")) {

			// IA OR HOD registration
			if (request.organizationId() == null && request.programDivisionId() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"OrganizationID and Program-Division ID is required for IA/HOD registration");
			}

		} else if (roleCode.equalsIgnoreCase("PD")) {

			// PD registration
			if (request.programDivisionId() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Programme-Division ID is required for PD registration");
			}

		}

		UserAccountEntity account = new UserAccountEntity();

		account.setRoleId(role.getRoleId());
		account.setOrganizationId(request.organizationId());
		account.setProgramDivisionId(request.programDivisionId());

		account.setFullName(request.fullName().trim());
		account.setEmail(email);
		account.setMobile(request.mobile().trim());
		account.setDesignation(request.designation());
		account.setSchemeId(request.schemeId());

		// The incoming value must be the raw password, never an existing hash.
		account.setPasswordHash(passwordEncoder.encode(request.password()));
		account.setAccountStatus("PENDING_APPROVAL");

		// Save account
		UserAccountEntity savedAccount = userAccountRepository.save(account);

		emailService.sendRegistrationEmail(savedAccount.getEmail(), savedAccount.getFullName(), request.password());

		return savedAccount;

	}

	@Override
	@Transactional
	public LoginResponse misLogin(LoginRequestDTO request) {

		UserAccountProjection user = userAccountRepository.findUserForAuthentication(request.username())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {

			throw new BadCredentialsException("Invalid email or password");
		}

		if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
			throw new DisabledException("Your account is not active.");
		}

		String email = user.getEmail();
		Long userId = user.getUserId();
		String roleCode = user.getRoleCode();

		// Every login generates a new unique token ID.
		String tokenId = UUID.randomUUID().toString();

		Instant expiresAt = Instant.now().plusMillis(expirationTime);

		OffsetDateTime expiresAtDB = expiresAt.atOffset(ZoneOffset.UTC);

		UserDetails userDetails = User.withUsername(email).password(user.getPasswordHash())
				.authorities("ROLE_" + roleCode).build();

		String token = jwtService.generateToken(userDetails, tokenId);

		// This overwrites the previous browser's active token.
		int updated = userAccountRepository.updateActiveSession(userId, tokenId, expiresAtDB);

		if (updated != 1) {
			throw new IllegalStateException("Unable to create login session.");
		}

		LoginResponse response = new LoginResponse();

		response.setAccessToken(token);
		response.setTokenType("Bearer");
		response.setRole(roleCode);
		response.setEmail(email);
		response.setExpiresAt(
				expiresAt.atZone(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		response.setMessage("Login successful!");

		return response;
	}

	@Override
	@Transactional
	public void logout(String email, String tokenId) {

		UserAccountEntity user = userAccountRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		int result = userAccountRepository.clearActiveSession(user.getUserId(), tokenId);

		if (result == 0) {
			throw new RuntimeException("Session already expired or replaced.");
		}
	}

	@Override
	public List<StateEntity> getAllStates() {

		return stateRepository.findAll();

	}

	@Override
	@Transactional
	public void forgotPassword(String email) {

		// 1. Find user by email
		UserAccountEntity user = userAccountRepository.findByEmailIgnoreCase(email).orElse(null);

		// Return generic success if account does not exist
		if (user == null) {
			return;
		}

		// 2. Generate OTP
		String otp = generateOtp();

		// 3. Hash OTP
		String otpHash = passwordEncoder.encode(otp);

		// 4. Invalidate previous unused reset requests
		passwordResetRepository.invalidatePreviousRequests(user.getUserId());

		// 5. Save new OTP request
		PasswordResetRequestEntity reset = new PasswordResetRequestEntity();

		reset.setUserId(user.getUserId());
		reset.setOtpHash(otpHash);
		reset.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
		reset.setAttempts(0);

		passwordResetRepository.save(reset);

		// Send OTP through email
		emailService.sendOtpEmail(email, otp);
	}

	@Override
	@Transactional
	public String verifyOtp(String email, String otp) {

		PasswordResetRequestEntity reset = passwordResetRepository.findLatestActiveRequest(email)
				.orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

		// Check OTP expiry
		if (reset.getExpiresAt().isBefore(OffsetDateTime.now())) {

			throw new IllegalArgumentException("OTP has expired");
		}

		// Check maximum attempts
		if (reset.getAttempts() >= 5) {
			throw new IllegalArgumentException("Maximum OTP attempts exceeded");
		}

		// Verify OTP using BCrypt
		if (!passwordEncoder.matches(otp, reset.getOtpHash())) {

			reset.setAttempts(reset.getAttempts() + 1);

			passwordResetRepository.save(reset);

			throw new IllegalArgumentException("Invalid OTP");
		}

		// Generate secure reset token
		String resetToken = generateResetToken();

		// Store SHA-256 hash instead of plain token
		reset.setResetTokenHash(sha256(resetToken));

		reset.setVerifiedAt(OffsetDateTime.now());

		// Reset token validity: 05 minutes
		reset.setResetTokenExpiresAt(OffsetDateTime.now().plusMinutes(5));

		passwordResetRepository.save(reset);

		return resetToken;
	}

	@Override
	@Transactional
	public void resetPassword(ResetPasswordRequest request) {

		// Generate SHA-256 hash of received reset token
		String tokenHash = sha256(request.resetToken());

		// Find reset request using token hash
		PasswordResetRequestEntity reset = passwordResetRepository.findByResetTokenHash(tokenHash)
				.orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

		OffsetDateTime now = OffsetDateTime.now();

		System.out.println("Reset request details: " + reset.getConsumedAt() + ", " + reset.getVerifiedAt() + ", "
				+ reset.getResetTokenExpiresAt());

		// Check whether token is already used
		if (reset.getConsumedAt() != null) {
			throw new IllegalArgumentException("Reset token has already been used");
		}

		// Check whether OTP was verified
		if (reset.getVerifiedAt() == null) {
			throw new IllegalArgumentException("OTP verification is required");
		}

		// Check reset token expiry
		if (reset.getResetTokenExpiresAt() == null || reset.getResetTokenExpiresAt().isBefore(now)) {
			throw new IllegalArgumentException("Reset token has expired");
		}

		// Validate new password
		if (request.newPassword() == null || request.newPassword().length() < 8) {
			throw new IllegalArgumentException("Password must contain at least 8 characters");
		}

		if (!request.newPassword().equals(request.confirmPassword())) {
			throw new IllegalArgumentException("Password and confirm password do not match");
		}

		// Fetch user account using userId
		UserAccountEntity user = userAccountRepository.findById(reset.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("User account not found"));

		// Encode and update password
		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		// Invalidate existing login sessions by clearing activeTokenId
		user.setActiveTokenId(null);
		user.setPasswordChangedAt(now);

		// Mark reset request as consumed
		reset.setConsumedAt(now);

		// Save updated records
		userAccountRepository.save(user);
		passwordResetRepository.save(reset);
	}

	private String generateOtp() {

		int otp = SECURE_RANDOM.nextInt(1_000_000);

		return String.format("%06d", otp);
	}

	private String generateResetToken() {

		byte[] bytes = new byte[32];

		SECURE_RANDOM.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String sha256(String value) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

			return HexFormat.of().formatHex(hash);

		} catch (NoSuchAlgorithmException ex) {

			throw new IllegalStateException("SHA-256 algorithm not available", ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProposalListingProjectionResponse getProposalDetailsById(Long schemeId) {

		return nodalOfficerRepository.findProposalDetailsBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Proposal not found for schemeId: " + schemeId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProposalListingProjectionResponse> getAllProposalDetails() {
		return nodalOfficerRepository.findAllProposalDetails();
	}

	@Override
	@Transactional(readOnly = true)
	public List<NodalOfficerDetailsDTO> getNodalOfficerDetailsBySchemeId(Long schemeId) {

		// Fetch nodal officer details
		List<NodalOfficerDetailsResponse> officers = nodalOfficerRepository.findNodalOfficerDetailsBySchemeId(schemeId);

		if (officers.isEmpty()) {
			throw new RuntimeException("Nodal officer details not found for scheme ID: " + schemeId);
		}

		// Fetch state-wise districts
		List<Object[]> stateDistrictRows = nodalOfficerRepository.findStateWiseDistricts(schemeId);

		// Group districts state-wise
		Map<String, List<String>> stateDistrictMap = new LinkedHashMap<>();

		for (Object[] row : stateDistrictRows) {

			String stateName = (String) row[0];
			String districtName = (String) row[1];

			stateDistrictMap.computeIfAbsent(stateName, key -> new ArrayList<>()).add(districtName);
		}

		// Convert into nested DTO
		List<StateDistrictResponse> implementingStates = stateDistrictMap.entrySet().stream()
				.map(entry -> new StateDistrictResponse(entry.getKey(), entry.getValue())).toList();

		// Map nodal officers with state-wise districts
		return officers.stream()
				.map(officer -> new NodalOfficerDetailsDTO(officer.getOfficerName(), officer.getDesignation(),
						officer.getEmail(), officer.getMobile(), officer.getAadhaarNumber(),
						officer.getDateOfProposal(), officer.getExpectedCompletionDate(),
						officer.getProposedFinancialOutlay(), officer.getSchemeDuration(), implementingStates))
				.toList();
	}

}