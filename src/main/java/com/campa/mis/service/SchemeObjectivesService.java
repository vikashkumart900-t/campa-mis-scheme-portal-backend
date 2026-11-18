package com.campa.mis.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.ObjectiveDetailsRequest;
import com.campa.mis.dto.response.ObjectiveDetailsResponse;
import com.campa.mis.dto.response.SchemeObjectivesResponse;
import com.campa.mis.dto.response.StateFundingResponse;

public interface SchemeObjectivesService {

	ObjectiveDetailsResponse saveObjectivesDetails(Long schemeId, ObjectiveDetailsRequest request,
			MultipartFile stateAgreementFile, MultipartFile fundingDocumentFile) throws IOException;

	List<SchemeObjectivesResponse> getObjectives(Long schemeId);

	StateFundingResponse getStateFunding(Long schemeId);

//	List<SchemeObjectivesResponse> saveObjectives(Long schemeId, SchemeObjectivesRequest request);

//	StateFundingResponse saveStateFunding(Long schemeId, StateFundingRequest request, MultipartFile stateAgreementFile,
//			MultipartFile fundingDocumentFile) throws IOException;

}