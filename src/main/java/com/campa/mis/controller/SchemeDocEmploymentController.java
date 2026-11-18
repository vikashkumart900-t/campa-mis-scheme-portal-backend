package com.campa.mis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.SchemeSupportingRequest;
import com.campa.mis.service.SchemeDocEmploymentService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeDocEmploymentController {

	private final SchemeDocEmploymentService docEmploymentService;
	private final ObjectMapper objectMapper;

	@PostMapping(value = "/{schemeId}/document-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> saveDocumentsEmploymentDetails(

			@PathVariable Long schemeId,

			@RequestPart("request") String requestJson,

			@RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

		SchemeSupportingRequest request = objectMapper.readValue(requestJson, SchemeSupportingRequest.class);

		return ResponseEntity.ok(this.docEmploymentService.saveDocEmploymentDetails(schemeId, request, files));
	}
}