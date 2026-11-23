package com.campa.mis.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.ObjectiveDetailsRequest;
import com.campa.mis.dto.response.ObjectiveDetailsResponse;
import com.campa.mis.dto.response.SchemeFundingObjectivesResponse;

public interface SchemeObjectivesService {

	ObjectiveDetailsResponse saveObjectivesDetails(Long schemeId, ObjectiveDetailsRequest request,
			MultipartFile stateAgreementFile, MultipartFile fundingDocumentFile) throws IOException;

	SchemeFundingObjectivesResponse getFundingAndObjectives(Long schemeId);

}