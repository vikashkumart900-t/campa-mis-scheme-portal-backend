package com.campa.mis.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.service.SchemeDeliverablesService;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeDeliverablesController {

	private final SchemeDeliverablesService schemeDeliverablesService;
	private final ObjectMapper objectMapper;

	// SAVE OR UPDATE
	@PostMapping(value = "/{schemeId}/deliverables", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> saveDeliverables(@PathVariable Long schemeId, @RequestPart("request") String requestJson,
			@RequestPart(value = "file", required = false) MultipartFile file) {

		DeliverablesRequest request = objectMapper.readValue(requestJson, DeliverablesRequest.class);

		return ResponseEntity.ok(schemeDeliverablesService.saveDeliverables(schemeId, request, file));
	}

	// FETCH
	@GetMapping("/deliverables/{schemeId}")
	public ResponseEntity<?> getDeliverables(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.schemeDeliverablesService.getDeliverables(schemeId));
	}

}