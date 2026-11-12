package com.campa.mis.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NodalOfficerRegistrationRequestDTO(
		
		String officerName, 
		String designation, 
		String email, 
		String mobile,
	    String aadhaarNumber,
		String schemeDuration,
		BigDecimal proposedFinancialOutlay, 
		LocalDate dateOfProposal,
		LocalDate expectedCompletionDate,
		Long organizationId,
		Long divisionId,
		Long schemeId
		
		
		) 
{}
