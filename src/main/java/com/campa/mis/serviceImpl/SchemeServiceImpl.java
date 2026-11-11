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
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.request.ResetPasswordRequest;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.dto.response.UserAccountProjection;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.NodalOfficerEntity;
import com.campa.mis.entity.OrganizationEntity;
import com.campa.mis.entity.PasswordResetRequestEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.repo.DistrictRepository;
import com.campa.mis.repo.NodalOfficerRepository;
import com.campa.mis.repo.OrganizationRepository;
import com.campa.mis.repo.PasswordResetRequestRepository;
import com.campa.mis.repo.ProgramDivisionRepository;
import com.campa.mis.repo.SchemeRepository;
import com.campa.mis.repo.StateRepository;
import com.campa.mis.repo.SubAgencyRepository;
import com.campa.mis.repo.UserAccountRepository;
import com.campa.mis.repo.UserRoleRepository;
import com.campa.mis.security.JwtService;
import com.campa.mis.service.EmailService;
import com.campa.mis.service.SchemeService;

@Service
public class SchemeServiceImpl implements SchemeService {

	private final RestTemplate restTemplate;

	@Value("${parivesh.base-url}")
	private String baseUrl;

	@Value("${parivesh.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration-ms}")
	private long expirationTime;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final OrganizationRepository organizationRepository;
	private final StateRepository stateRepository;
	private final DistrictRepository districtRepository;
	private final SubAgencyRepository subAgencyRepository;
	private final ProgramDivisionRepository programDivisionRepository;
	private final NodalOfficerRepository nodalOfficerRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SchemeRepository schemeRepository;
	private final UserRoleRepository userRoleRepository;
	private final UserAccountRepository userAccountRepository;
	private final JwtService jwtService;
	private final PasswordResetRequestRepository passwordResetRepository;
	private final EmailService emailService;

	public SchemeServiceImpl(OrganizationRepository organizationRepository, StateRepository stateRepository,
			DistrictRepository districtRepository, SubAgencyRepository subAgencyRepository,
			ProgramDivisionRepository programDivisionRepository, NodalOfficerRepository nodalOfficerRepository,
			SchemeRepository schemeRepository, UserRoleRepository userRoleRepository,
			UserAccountRepository userAccountRepository, JwtService jwtService,
			PasswordResetRequestRepository passwordResetRepository, EmailService emailService) {

		this.organizationRepository = organizationRepository;
		this.stateRepository = stateRepository;
		this.districtRepository = districtRepository;
		this.subAgencyRepository = subAgencyRepository;
		this.programDivisionRepository = programDivisionRepository;
		this.nodalOfficerRepository = nodalOfficerRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
		this.schemeRepository = schemeRepository;
		this.restTemplate = new RestTemplate();
		this.userRoleRepository = userRoleRepository;
		this.userAccountRepository = userAccountRepository;
		this.jwtService = jwtService;
		this.passwordResetRepository = passwordResetRepository;
		this.emailService = emailService;
	}

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
	public String saveNodalOfficerData(NodalOfficerRegistrationRequestDTO request) {

		if (request == null) {
			throw new IllegalArgumentException("Registration request is required");
		}

		if (request.organizationId() == null) {
			throw new IllegalArgumentException("Organization ID is required");
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

		if (request.password() == null || request.password().isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}

		// Verify that the selected organization exists
		organizationRepository.findById(request.organizationId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid organization ID"));

		NodalOfficerEntity officer = new NodalOfficerEntity();

		officer.setOfficerName(request.officerName().trim());
		officer.setDesignation(request.designation().trim());
		officer.setEmail(request.email().trim());
		officer.setMobile(request.mobile().trim());
		officer.setOrganizationId(request.organizationId());
		officer.setAadhaarNumber(request.aadhaarNumber());
		officer.setCreatedAt(OffsetDateTime.now());
		officer.setExpectedCompletionDate(request.expectedCompletionDate());
		officer.setDateOfProposal(request.dateOfProposal());
		officer.setProposedFinancialOutlay(request.proposedFinancialOutlay());
		officer.setSchemeDuration(request.schemeDuration());
		officer.setOrgName(request.orgName());

		nodalOfficerRepository.save(officer);

		return "Nodal officer registered successfully";
	}

	@Override
	public List<SchemeEntity> getAllActiveSchemes() {

		return schemeRepository.findAll();

	}

	@Override
	public ResponseEntity<String> pariveshLogin(LoginRequestDTO request) {

		String url = UriComponentsBuilder.fromUriString(baseUrl).path("/api/integration/login")
				.queryParam("secretKey", secretKey).build().encode().toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

		HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);

		try {
			return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		} catch (RestClientResponseException ex) {
			// Forward the external API's HTTP status and response body.
			return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
					.body(ex.getResponseBodyAsString());

		} catch (RestClientException ex) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("{\"message\":\"Unable to connect to PARIVESH service\"}");
		}
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

			return new SchemeDetailsResponse(schemeName, organizationName, divisionName);
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

		if (!roleCode.equals("IA") && !roleCode.equals("PD")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only IA and PD registration is supported");
		}

		var role = userRoleRepository.findByRoleCode(roleCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleCode));

		if (roleCode.equals("Implementing Agency")) {
			if (request.organizationId() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Organization is required for IA registration");
			}
		} else {
			// Programme Division registration does not require an organization.

			if (request.programDivisionId() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Programme Division is required for PD registration");
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

		// The incoming value must be the raw password, never an existing hash.
		account.setPasswordHash(passwordEncoder.encode(request.password()));

		account.setAccountStatus("PENDING_APPROVAL");

		emailService.sendRegistrationEmail(email, request.email(), request.password());

		return userAccountRepository.save(account);
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
}