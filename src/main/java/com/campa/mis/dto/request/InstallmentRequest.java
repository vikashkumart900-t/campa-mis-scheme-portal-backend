package com.campa.mis.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record InstallmentRequest(

    @NotBlank
    String financialYear,

    @NotNull
    @Positive
    Integer installmentNo,

    @NotBlank
    String duration,

    @NotNull
    @DecimalMin("0.01")
    BigDecimal amountInLakh

) {}