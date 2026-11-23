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

import com.campa.mis.dto.request.ProposalTechnologyExitRequest;
import com.campa.mis.dto.response.ProposalTechnologyExitResponse;
import com.campa.mis.service.ProposalTechnologyExitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class ProposalTechnologyExitController {

	private final ProposalTechnologyExitService proposalTechnologyExitService;
	private final ObjectMapper objectMapper;

	// CREATE
	@PostMapping(value = "/{schemeId}/tech-exit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProposalTechnologyExitResponse> create(@PathVariable Long schemeId,
			@Valid @RequestPart("request") String request,

			@RequestPart("technologicalDocument") MultipartFile technologicalDocument,

			@RequestPart("exitProtocolDocument") MultipartFile exitProtocolDocument) throws IOException {

		ProposalTechnologyExitRequest exitRequest = objectMapper.readValue(request,
				ProposalTechnologyExitRequest.class);

		return ResponseEntity.ok(this.proposalTechnologyExitService.saveProposalTechExit(schemeId, exitRequest,
				technologicalDocument, exitProtocolDocument));
	}

	// GET TECHNOLOGY EXIT DETAILS BY SCHEME ID
	@GetMapping("/tech-exit/{schemeId}")
	public ResponseEntity<ProposalTechnologyExitResponse> getProposalTechExit(@PathVariable Long schemeId) {

		return ResponseEntity.ok(proposalTechnologyExitService.getProposalTechExitBySchemeId(schemeId));
	}

}
