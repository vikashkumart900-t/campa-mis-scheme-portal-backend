package com.campa.mis.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BankDetailsResponse(

    Integer bankId,
    String accountName,
    String bankName,
    String ifscCode,
    String branchNameAddress,
    String accountNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<InstallmentResponse> installments

) {

    public record InstallmentResponse(
        Integer installmentId,
        String financialYear,
        Integer installmentNo,
        String duration,
        BigDecimal amountInLakh,
        LocalDateTime createdAt
    ) {}
}