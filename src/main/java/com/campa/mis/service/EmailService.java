package com.campa.mis.service;

import com.campa.mis.entity.StateHodAccountEntity;

public interface EmailService {

	void sendOtpEmail(String toEmail, String otp);

	void sendRegistrationEmail(String toEmail, String username, String password);

	void sendStateHodRegistrationEmail(String to, String name, String password);

	void sendStateHodNotification(StateHodAccountEntity account, String schemeName);

}