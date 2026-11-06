package com.campa.mis.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.campa.mis.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String senderEmail;

	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendOtpEmail(String toEmail, String otp) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(senderEmail);
		message.setTo(toEmail);
		message.setSubject("CAMPA SCHEME-MIS Portal - Password Reset OTP");

		message.setText("Dear User,\n\n" + "We received a request to reset your CAMPA MIS Portal password.\n\n"
				+ "Your One-Time Password (OTP) is: " + otp + "\n\n" + "This OTP is valid for 5 minutes.\n\n"
				+ "Please do not share this OTP with anyone.\n\n"
				+ "If you did not request a password reset, please ignore this email.\n\n" + "Regards,\n"
				+ "CAMPA MIS Portal Team");

		mailSender.send(message);
	}
}