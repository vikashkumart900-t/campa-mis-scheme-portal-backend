package com.campa.mis.service;

import java.util.List;

import com.campa.mis.dto.request.BulkStateHodEmailRequest;
import com.campa.mis.dto.request.StateHodRegistrationRequest;
import com.campa.mis.dto.response.BulkStateHodEmailResponse;
import com.campa.mis.dto.response.StateHodRegistrationResponse;

public interface StateHodAccountService {

	StateHodRegistrationResponse registerStateHod(StateHodRegistrationRequest request);

	BulkStateHodEmailResponse bulkSendEmail(BulkStateHodEmailRequest request);
	
	List<StateHodRegistrationResponse> getStateHodStatus(Long schemeId);
}
