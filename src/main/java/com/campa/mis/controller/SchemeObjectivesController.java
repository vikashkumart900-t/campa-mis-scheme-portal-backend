package com.campa.mis.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.ObjectiveDetailsRequest;
import com.campa.mis.dto.response.SchemeFundingObjectivesResponse;
import com.campa.mis.service.SchemeObjectivesService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeObjectivesController {

	private final SchemeObjectivesService schemeObjectivesService;
	private final ObjectMapper objectMapper;

	@PostMapping(value = "/{schemeId}/objective-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> saveObjectiveDetails(@PathVariable Long schemeId,

			@RequestParam("request") String requestJson,

			@RequestPart(value = "stateAgreementFile", required = false) MultipartFile stateAgreementFile,

			@RequestPart(value = "fundingDocumentFile", required = false) MultipartFile fundingDocumentFile)
			throws IOException {

		ObjectiveDetailsRequest request = objectMapper.readValue(requestJson, ObjectiveDetailsRequest.class);

		return ResponseEntity.ok(schemeObjectivesService.saveObjectivesDetails(schemeId, request, stateAgreementFile,
				fundingDocumentFile));
	}
	
	@GetMapping("/objectives/{schemeId}")
	public ResponseEntity<SchemeFundingObjectivesResponse> getFundingAndObjectives(
	        @PathVariable Long schemeId) {

	    return ResponseEntity.ok(
	    		schemeObjectivesService.getFundingAndObjectives(schemeId)
	    );
	}

}