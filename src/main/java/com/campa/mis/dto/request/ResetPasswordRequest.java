package com.campa.mis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(@NotBlank String resetToken,

		
		@NotBlank(message = "New password is required")
		@Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
		@Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$",
		    message = "Password must contain at least one uppercase, one lowercase, one number and one special character"
		)
		String newPassword,

		@NotBlank(message = "Confirm password is required")
		@Size(min = 8, max = 16, message = "Confirm password must be between 8 and 16 characters")
		String confirmPassword
		
		
		) {
}