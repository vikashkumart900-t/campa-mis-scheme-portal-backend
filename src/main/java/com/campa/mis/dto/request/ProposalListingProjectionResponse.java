package com.campa.mis.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProposalListingProjectionResponse {

	Long getSchemeId();
	
    String getSchemeName();

    String getSchemeDuration();

    BigDecimal getProposedFinancialOutlay();

    String getProposalId();

    String getHodStatus();

    String getPdStatus();

    String getCampaStatus();
    
    LocalDateTime getSubmissionDate();
}