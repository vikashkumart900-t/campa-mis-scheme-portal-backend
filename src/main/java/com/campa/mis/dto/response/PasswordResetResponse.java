package com.campa.mis.dto.response;

public record PasswordResetResponse(boolean success, String message, String resetToken) {
}