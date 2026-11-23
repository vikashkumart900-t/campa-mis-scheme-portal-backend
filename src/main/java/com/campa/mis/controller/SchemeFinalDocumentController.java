package com.campa.mis.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.SchemeRoleStatusUpdateRequest;
import com.campa.mis.dto.response.SchemeFinalDocumentResponse;
import com.campa.mis.entity.SchemeRoleStatusEntity;
import com.campa.mis.service.SchemeFinalDocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeFinalDocumentController {

	private final SchemeFinalDocumentService finalDocumentService;

	// POST: UPLOAD / UPDATE FINAL DOCUMENTS
	@PostMapping(value = "/{schemeId}/final-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SchemeFinalDocumentResponse> saveFinalDocuments(

			@PathVariable Long schemeId,

			@RequestParam(value = "signatureFile", required = false) MultipartFile signatureFile,

			@RequestParam(value = "supportingDocument", required = false) MultipartFile supportingDocument) {

		SchemeFinalDocumentResponse response = this.finalDocumentService.saveFinalDocuments(schemeId, signatureFile,
				supportingDocument);

		return ResponseEntity.ok(response);
	}

	// GET: FETCH FINAL DOCUMENTS
	@GetMapping("/{schemeId}/final-documents")
	public ResponseEntity<SchemeFinalDocumentResponse> getFinalDocuments(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.finalDocumentService.getFinalDocuments(schemeId));
	}

	@GetMapping("/schemeRoleStatusByID/{schemeId}")
	public ResponseEntity<SchemeRoleStatusEntity> getSchemeRoleStatusById(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.finalDocumentService.getSchemeRoleStatusById(schemeId));
	}
	
	@GetMapping("/schemeRoleStatus")
	public ResponseEntity<List<SchemeRoleStatusEntity>> getSchemeRoleStatus() {

		return ResponseEntity.ok(this.finalDocumentService.getSchemeRoleStatus());
	}

	@PostMapping("/updateRoleStatus")
	public ResponseEntity<SchemeRoleStatusEntity> updateSchemeRoleStatus(
			@Valid @RequestBody SchemeRoleStatusUpdateRequest request) {

		SchemeRoleStatusEntity response = this.finalDocumentService.updateSchemeRoleStatus(request);

		return ResponseEntity.ok(response);
	}

}