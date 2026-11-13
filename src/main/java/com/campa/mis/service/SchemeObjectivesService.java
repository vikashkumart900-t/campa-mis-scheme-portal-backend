package com.campa.mis.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.SchemeObjectivesRequest;
import com.campa.mis.dto.request.StateFundingRequest;
import com.campa.mis.dto.response.DocumentUploadResponse;
import com.campa.mis.dto.response.SchemeObjectivesResponse;
import com.campa.mis.dto.response.StateFundingResponse;

public interface SchemeObjectivesService {

	List<SchemeObjectivesResponse> saveObjectives(Long schemeId, SchemeObjectivesRequest request);

	List<SchemeObjectivesResponse> getObjectives(Long schemeId);

	StateFundingResponse saveStateFunding(Long schemeId, StateFundingRequest request);

	StateFundingResponse getStateFunding(Long schemeId);

	DocumentUploadResponse uploadDocument(Long schemeId, String documentType, MultipartFile file);

	StateFundingResponse saveStateFunding(Long schemeId, Boolean hasStateComponents, Boolean stateOnboard,
			MultipartFile stateAgreementFile, Boolean hasOtherFundingAgency, String otherFundingSources,
			MultipartFile fundingDocumentFile) throws IOException;
}