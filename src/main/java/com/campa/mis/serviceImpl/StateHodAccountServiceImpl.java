
package com.campa.mis.serviceImpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campa.mis.dto.request.BulkStateHodEmailRequest;
import com.campa.mis.dto.request.StateHodRegistrationRequest;
import com.campa.mis.dto.response.BulkStateHodEmailResponse;
import com.campa.mis.dto.response.BulkStateHodEmailResponse.StateEmailResult;
import com.campa.mis.dto.response.StateHodRegistrationResponse;
import com.campa.mis.entity.StateHodAccountEntity;
import com.campa.mis.repo.SchemeRepository;
import com.campa.mis.repo.StateHodAccountRepository;
import com.campa.mis.repo.StateRepository;
import com.campa.mis.service.EmailService;
import com.campa.mis.service.StateHodAccountService;
import com.campa.mis.util.StateHodAsyncEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateHodAccountServiceImpl implements StateHodAccountService {

	private final StateHodAccountRepository accountRepository;
	private final SchemeRepository schemeRepository;
	private final StateRepository stateRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final StateHodAsyncEmailService stateHodAsyncEmailService;

	// REGISTER STATE HOD
	@Override
	@Transactional
	public StateHodRegistrationResponse registerStateHod(StateHodRegistrationRequest request) {

		var scheme = schemeRepository.findById(request.schemeId())
				.orElseThrow(() -> new IllegalArgumentException("Scheme not found"));

		stateRepository.findByStateCode(request.stateCode())
				.orElseThrow(() -> new IllegalArgumentException("State not found"));

		if (accountRepository.existsBySchemeIdAndStateCode(request.schemeId(), request.stateCode())) {

			throw new IllegalArgumentException("State HOD account already registered for this scheme and state");
		}

		StateHodAccountEntity account = new StateHodAccountEntity();

		account.setSchemeId(request.schemeId());
		account.setStateCode(request.stateCode());
		account.setName(request.fullName());
		account.setEmail(request.email().trim().toLowerCase());
		account.setPasswordHash(passwordEncoder.encode(request.password()));
		account.setMobileNumber(request.mobile());
		account.setDesignation(request.designation());
		account.setStatus(true);

		StateHodAccountEntity saved = accountRepository.save(account);

		// Send email asynchronously
		stateHodAsyncEmailService.sendRegistrationEmail(saved.getEmail(), saved.getName(), request.password());

		return new StateHodRegistrationResponse(saved.getId(), saved.getSchemeId(), saved.getStateCode(),
				saved.getName(), saved.getEmail(), saved.getMobileNumber(), saved.getDesignation(), saved.isStatus(),
				"State HOD registered successfully. Email is being processed.");
	}

	// BULK SEND EMAIL TO SELECTED STATES

	@Override
	@Transactional
	public BulkStateHodEmailResponse bulkSendEmail(BulkStateHodEmailRequest request) {

		// 1. Validate Scheme
		var scheme = schemeRepository.findById(request.schemeId())
				.orElseThrow(() -> new IllegalArgumentException("Scheme not found"));

		String schemeName = scheme.getSchemeName();

		// 2. Remove blank and duplicate state codes
		Set<String> uniqueStateCodes = request.stateCodes().stream().filter(code -> code != null && !code.isBlank())
				.map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new));

		if (uniqueStateCodes.isEmpty()) {
			throw new IllegalArgumentException("Please select at least one valid state");
		}

		// 3. Fetch registered accounts for selected states
		List<StateHodAccountEntity> accounts = accountRepository.findBySchemeIdAndStateCodeIn(request.schemeId(),
				new ArrayList<>(uniqueStateCodes));

		// 4. Create State Code Map
		Map<String, StateHodAccountEntity> accountMap = accounts.stream()
				.collect(Collectors.toMap(StateHodAccountEntity::getStateCode, Function.identity()));

		List<StateEmailResult> results = new ArrayList<>();

		int successCount = 0;
		int failedCount = 0;

		// 5. Process each selected state
		for (String stateCode : uniqueStateCodes) {

			// Fetch State Name
			var state = stateRepository.findByStateCode(stateCode).orElse(null);

			String stateName = state != null ? state.getStateName() : null;

			// Find account
			StateHodAccountEntity account = accountMap.get(stateCode);

			// 6. Account not registered
			if (account == null) {

				results.add(new StateEmailResult(stateCode, stateName, null, "FAILED",
						"State HOD account not registered for this scheme"));

				failedCount++;
				continue;
			}

			// 7. Send email
			try {

				emailService.sendStateHodNotification(account, schemeName);

				accountRepository.save(account);

				results.add(new StateEmailResult(stateCode, stateName, account.getEmail(), "SENT",
						"Email sent successfully"));

				successCount++;

			} catch (Exception ex) {

				results.add(new StateEmailResult(stateCode, stateName, account.getEmail(), "FAILED",
						"Unable to send email"));

				failedCount++;
			}
		}

		// 8. Final Response
		boolean success = failedCount == 0;

		String message = success ? "All state emails sent successfully"
				: "Email processing completed with some failures";

		return new BulkStateHodEmailResponse(success, message, request.schemeId(), uniqueStateCodes.size(),
				successCount, failedCount, results);
	}

	@Override
	public List<StateHodRegistrationResponse> getStateHodStatus(Long schemeId) {

		return accountRepository.findBySchemeId(schemeId).stream().map(account -> {

			return new StateHodRegistrationResponse(account.getId(), account.getSchemeId(), account.getStateCode(),
					account.getName(), account.getEmail(), account.getMobileNumber(), account.getDesignation(),
					account.isStatus(), account.isStatus() ? "Approved" : "Pending");

		}).toList();
	}

}
