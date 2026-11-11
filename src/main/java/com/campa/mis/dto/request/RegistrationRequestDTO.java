package com.campa.mis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDTO(

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "IA|PD", message = "Role must be IA or PD")
    String roleCode,

    Long organizationId,

    Long programDivisionId,

    @NotBlank(message = "Full name is required")
    @Size(max = 150)
    String fullName,

    @NotBlank(message = "Designation is required")
    @Size(max = 100)
    String designation,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 150)
    String email,

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Mobile number must contain 10 digits"
    )
    String mobile,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$",
		    message = "Password must contain at least one uppercase, one lowercase, one number and one special character"
		)
    String password

) {}