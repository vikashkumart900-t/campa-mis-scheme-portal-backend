package com.campa.mis.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    
    List<String> getStateNames();
}