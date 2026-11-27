package com.campa.mis.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.campa.mis.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateHodAsyncEmailService {

	private final EmailService emailService;

	@Async("stateHodEmailExecutor")
	public void sendRegistrationEmail(String email, String fullName, String password) {

		try {
			emailService.sendStateHodRegistrationEmail(email, fullName, password);
		} catch (Exception ex) {
			// Log the exception
			System.err.println("State HOD email failed: " + ex.getMessage());
		}
	}
}