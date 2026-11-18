package com.campa.mis.controller;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.EvaluationResearchRequest;
import com.campa.mis.dto.response.EvaluationResearchResponse;
import com.campa.mis.service.SchemeEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeEvaluationController {

	private final SchemeEvaluationService evaluationService;
	private final ObjectMapper objectMapper;

	// SAVE / UPDATE
	@PostMapping(value = "/{schemeId}/evaluation-research", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EvaluationResearchResponse> saveEvaluationResearch(

			@PathVariable Long schemeId,

			@Valid @RequestPart("request") String requestJson,

			@RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

		EvaluationResearchRequest researchRequest=objectMapper.readValue(requestJson, EvaluationResearchRequest.class);
		
		return ResponseEntity.ok(evaluationService.saveEvaluationResearch(schemeId, researchRequest, file));
	}

	// GET DETAILS
	@GetMapping("/{schemeId}/evaluation-research")
	public ResponseEntity<EvaluationResearchResponse> getEvaluationResearch(@PathVariable Long schemeId) {

		return ResponseEntity.ok(evaluationService.getEvaluationResearch(schemeId));
	}

}