package com.campa.mis.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DeliverablesResponse(

    Long id,

    String expenditureType,

    BigDecimal totalOutlay,

    Integer totalDeliverables,

    List<DeliverableResponse> deliverables,

    List<YearlyExpenditureResponse> yearlyExpenditures,

    List<String> nationalImportance,

    List<String> internationalFootprints

) {

    public record DeliverableResponse(
        Long id,
        String deliverable,
        String unit,
        LocalDate initiationDate,
        LocalDate closingDate,
        BigDecimal financialOutlay,
        String remarks,
        String documentFileName,
        String documentFilePath
    ) {}

    public record YearlyExpenditureResponse(
        Long id,
        String financialYear,
        BigDecimal expenditure
    ) {}
}