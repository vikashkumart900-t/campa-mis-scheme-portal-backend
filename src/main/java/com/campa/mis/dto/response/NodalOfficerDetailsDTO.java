package com.campa.mis.dto.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record NodalOfficerDetailsDTO(
    String officerName,
    String designation,
    String email,
    String mobile,
    String aadhaarNumber,
    LocalDate dateOfProposal,
    LocalDate expectedCompletionDate,
    BigDecimal proposedFinancialOutlay,
    String schemeDuration,
    List<StateDistrictResponse> schemeImplementingStates
) {}