package com.campa.mis.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record BankDetailsRequest(

    @NotBlank
    String accountName,

    @NotBlank
    String bankName,

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
             message = "Invalid IFSC code")
    String ifscCode,

    @NotBlank
    String branchNameAddress,

    @NotBlank
    String accountNumber,

    @NotEmpty
    List<@Valid InstallmentRequest> installments

) {}