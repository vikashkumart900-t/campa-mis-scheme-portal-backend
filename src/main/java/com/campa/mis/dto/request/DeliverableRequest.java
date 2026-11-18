package com.campa.mis.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeliverableRequest(

    Long id,

    String deliverable,

    String unit,

    LocalDate initiationDate,

    LocalDate closingDate,

    BigDecimal financialOutlay,

    String remarks

) {}