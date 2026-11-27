package com.campa.mis.service;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.EcGbDecisionRequest;
import com.campa.mis.dto.response.EcDecisionResponse;
import com.campa.mis.dto.response.EcGbDecisionResponse;
import com.campa.mis.dto.response.GbDecisionResponse;

public interface EcGbDecisionService {

	EcGbDecisionResponse saveEcGbDecision(Long schemeId, EcGbDecisionRequest request, MultipartFile ecMinutesFile,
			MultipartFile gbMinutesFile);

//	EcDecisionResponse saveEcDecision(Long schemeId, EcDecisionRequest request, MultipartFile minutesFile);

	EcDecisionResponse getEcDecision(Long schemeId);

	byte[] downloadEcMinutes(Long schemeId);

//	GbDecisionResponse saveGbDecision(Long schemeId, GbDecisionRequest request, MultipartFile minutesFile);

	GbDecisionResponse getGbDecision(Long schemeId);

	byte[] downloadGbMinutes(Long schemeId);

}