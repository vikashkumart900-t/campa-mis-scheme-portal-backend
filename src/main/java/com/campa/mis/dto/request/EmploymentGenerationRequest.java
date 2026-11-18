package com.campa.mis.dto.request;

import java.math.BigDecimal;

public record EmploymentGenerationRequest(

    String financialYear,

    BigDecimal labourCost,

    BigDecimal mandaysGenerated,

    Integer tribals,

    Integer forestDwellersBenefitted

) {}