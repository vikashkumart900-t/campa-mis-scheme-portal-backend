package com.campa.mis.controller;

import java.util.Set;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.EcDecisionRequest;
import com.campa.mis.dto.request.EcGbDecisionRequest;
import com.campa.mis.dto.request.GbDecisionRequest;
import com.campa.mis.dto.response.EcDecisionResponse;
import com.campa.mis.dto.response.EcGbDecisionResponse;
import com.campa.mis.dto.response.GbDecisionResponse;
import com.campa.mis.service.EcGbDecisionService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
@Validated
public class EcGbDecisionController {

	private final EcGbDecisionService decisionService;
	private final ObjectMapper objectMapper;
	private final Validator validator;

	// ================= EC + GB DECISION =================

	@PostMapping(value = "/ec-gb-decision/{schemeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EcGbDecisionResponse> saveEcGbDecision(

			@PathVariable Long schemeId,

			@RequestPart("request") String jsonRequest,

			@RequestPart(value = "ecMinutesFile", required = false) MultipartFile ecMinutesFile,

			@RequestPart(value = "gbMinutesFile", required = false) MultipartFile gbMinutesFile

	) {

		EcGbDecisionRequest request = objectMapper.readValue(jsonRequest, EcGbDecisionRequest.class);

		// Validate nested DTOs
		Set<ConstraintViolation<EcGbDecisionRequest>> violations = validator.validate(request);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		return ResponseEntity.ok(decisionService.saveEcGbDecision(schemeId, request, ecMinutesFile, gbMinutesFile));
	}

	// ================= EC DECISION =================

//	@PostMapping(value = "/ec-decision/{schemeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<EcDecisionResponse> saveEcDecision(@PathVariable Long schemeId,
//			@Valid @RequestPart("request") String jsonRequest,
//			@RequestPart(value = "minutesFile", required = false) MultipartFile minutesFile) {
//
//		EcDecisionRequest request = objectMapper.readValue(jsonRequest, EcDecisionRequest.class);
//
//		return ResponseEntity.ok(decisionService.saveEcDecision(schemeId, request, minutesFile));
//	}

	@GetMapping("/ec-decision/{schemeId}")
	public ResponseEntity<EcDecisionResponse> getEcDecision(@PathVariable Long schemeId) {

		return ResponseEntity.ok(decisionService.getEcDecision(schemeId));
	}

	@GetMapping("/ec-decision/minutes/{schemeId}")
	public ResponseEntity<byte[]> downloadEcMinutes(@PathVariable Long schemeId) {

		byte[] file = decisionService.downloadEcMinutes(schemeId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.attachment().filename("EC_Meeting_Minutes.pdf").build());

		return ResponseEntity.ok().headers(headers).body(file);
	}

	// ================= GB DECISION =================

//	@PostMapping(value = "/gb-decision/{schemeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<GbDecisionResponse> saveGbDecision(@PathVariable Long schemeId,
//			@Valid @RequestPart("request") String jsonRequest,
//			@RequestPart(value = "minutesFile", required = false) MultipartFile minutesFile) {
//
//		GbDecisionRequest request = objectMapper.readValue(jsonRequest, GbDecisionRequest.class);
//
//		return ResponseEntity.ok(decisionService.saveGbDecision(schemeId, request, minutesFile));
//	}

	@GetMapping("/gb-decision/{schemeId}")
	public ResponseEntity<GbDecisionResponse> getGbDecision(@PathVariable Long schemeId) {

		return ResponseEntity.ok(decisionService.getGbDecision(schemeId));
	}

	@GetMapping("/gb-decision/minutes/{schemeId}")
	public ResponseEntity<byte[]> downloadGbMinutes(@PathVariable Long schemeId) {

		byte[] file = decisionService.downloadGbMinutes(schemeId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.attachment().filename("GB_Meeting_Minutes.pdf").build());

		return ResponseEntity.ok().headers(headers).body(file);
	}

}