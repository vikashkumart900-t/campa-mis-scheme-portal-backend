package com.campa.mis.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
    
    void sendRegistrationEmail(String toEmail,String username,String password);

}