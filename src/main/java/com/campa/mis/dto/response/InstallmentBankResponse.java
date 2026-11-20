package com.campa.mis.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InstallmentBankResponse(
        Integer installmentId,
        Integer bankId,
        String financialYear,
        Integer installmentNo,
        String duration,
        BigDecimal amountInLakh,
        String accountName,
        String bankName,
        String ifscCode,
        String branchNameAddress,
        String accountNumber,
        LocalDateTime createdAt
) {}