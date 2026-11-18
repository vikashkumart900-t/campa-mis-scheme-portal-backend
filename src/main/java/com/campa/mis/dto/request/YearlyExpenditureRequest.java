package com.campa.mis.dto.request;

import java.math.BigDecimal;

public record YearlyExpenditureRequest(

    String financialYear,

    BigDecimal expenditure

) {}