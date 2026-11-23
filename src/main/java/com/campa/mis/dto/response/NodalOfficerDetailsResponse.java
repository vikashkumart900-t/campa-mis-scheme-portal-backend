package com.campa.mis.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface NodalOfficerDetailsResponse {

	String getOfficerName();

	String getDesignation();

	String getEmail();

	String getMobile();

	String getAadhaarNumber();

	LocalDate getDateOfProposal();

	LocalDate getExpectedCompletionDate();

	BigDecimal getProposedFinancialOutlay();

	String getSchemeDuration();

	String getSchemeImplementingStates();

}
