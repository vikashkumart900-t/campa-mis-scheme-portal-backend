package com.campa.mis.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InstallmentRequest(

    @NotBlank
    String financialYear,

    @NotNull
    @Positive
    Integer installmentNo,

    @NotBlank
    String duration,

    @NotNull
    BigDecimal amountInLakh

) {}