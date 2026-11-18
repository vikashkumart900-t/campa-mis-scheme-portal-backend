package com.campa.mis.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.EvaluationResearchRequest;
import com.campa.mis.dto.response.EvaluationResearchResponse;

public interface SchemeEvaluationService {

	EvaluationResearchResponse saveEvaluationResearch(Long schemeId, EvaluationResearchRequest request,
			MultipartFile file) throws IOException;

	EvaluationResearchResponse getEvaluationResearch(Long schemeId);

}