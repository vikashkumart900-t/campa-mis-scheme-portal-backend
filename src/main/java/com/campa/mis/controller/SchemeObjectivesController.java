package com.campa.mis.controller;

import com.campa.mis.dto.request.SchemeObjectivesRequest;
import com.campa.mis.dto.request.StateFundingRequest;
import com.campa.mis.dto.response.DocumentUploadResponse;
import com.campa.mis.dto.response.SchemeObjectivesResponse;
import com.campa.mis.dto.response.StateFundingResponse;
import com.campa.mis.service.SchemeObjectivesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeObjectivesController {

	private final SchemeObjectivesService schemeObjectivesService;

	// SAVE OBJECTIVES
	@PostMapping("/{schemeId}/objectives")
	public ResponseEntity<List<SchemeObjectivesResponse>> saveObjectives(@PathVariable Long schemeId,
			@RequestBody SchemeObjectivesRequest request) {

		return ResponseEntity.ok(this.schemeObjectivesService.saveObjectives(schemeId, request));
	}

	// FETCH OBJECTIVES
	@GetMapping("/{schemeId}/objectives")
	public ResponseEntity<List<SchemeObjectivesResponse>> getObjectives(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.schemeObjectivesService.getObjectives(schemeId));
	}

	// SAVE STATE AND FUNDING DETAILS
	@PostMapping("/{schemeId}/state-funding")
	public ResponseEntity<StateFundingResponse> saveStateFunding(@PathVariable Long schemeId,
			@RequestBody StateFundingRequest request) {

		return ResponseEntity.ok(this.schemeObjectivesService.saveStateFunding(schemeId, request));
	}

	// FETCH STATE AND FUNDING DETAILS
	@GetMapping("/{schemeId}/state-funding")
	public ResponseEntity<StateFundingResponse> getStateFunding(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.schemeObjectivesService.getStateFunding(schemeId));
	}

	// UPLOAD SUPPORTING DOCUMENT
	@PostMapping(value = "/{schemeId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<DocumentUploadResponse> uploadDocument(@PathVariable Long schemeId,
			@RequestParam String documentType, @RequestParam MultipartFile file) {

		return ResponseEntity.ok(this.schemeObjectivesService.uploadDocument(schemeId, documentType, file));
	}

	@PostMapping(value = "/{schemeId}/state-funding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> saveStateFunding(

			@PathVariable Long schemeId,

			@RequestParam Boolean hasStateComponents,

			@RequestParam(required = false) Boolean stateOnboard,

			@RequestParam(required = false) MultipartFile stateAgreementFile,

			@RequestParam Boolean hasOtherFundingAgency,

			@RequestParam(required = false) String otherFundingSources,

			@RequestParam(required = false) MultipartFile fundingDocumentFile) {

		try {
			return ResponseEntity.ok(this.schemeObjectivesService.saveStateFunding(schemeId, hasStateComponents,
					stateOnboard, stateAgreementFile, hasOtherFundingAgency, otherFundingSources, fundingDocumentFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(500).body("Error occurred while saving state funding details.");
	}
}