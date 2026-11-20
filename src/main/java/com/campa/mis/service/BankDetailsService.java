package com.campa.mis.service;

import java.util.List;

import com.campa.mis.dto.request.BankDetailsRequest;
import com.campa.mis.dto.response.BankDetailsResponse;

public interface BankDetailsService {

	BankDetailsResponse saveBankDetails(BankDetailsRequest request,Long schemeId);
	
	BankDetailsResponse updateBankDetails(Integer bankId, BankDetailsRequest request);
	
	List<BankDetailsResponse> getAllBankDetails();
	
	BankDetailsResponse getBankDetails(Integer bankId);

}
