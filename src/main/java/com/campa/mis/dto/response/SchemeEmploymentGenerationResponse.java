package com.campa.mis.dto.response;

import java.math.BigDecimal;

public record SchemeEmploymentGenerationResponse(
        String financialYear,
        BigDecimal labourCost,
        BigDecimal mandaysGenerated,
        Integer tribals,
        Integer forestDwellersBenefitted
) {}