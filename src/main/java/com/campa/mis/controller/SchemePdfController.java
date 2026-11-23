package com.campa.mis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.campa.mis.service.SchemePdfService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemePdfController {

	private final SchemePdfService schemePdfService;

	@GetMapping("/{schemeId}/generate-pdf")
	public ResponseEntity<byte[]> generatePdf(@PathVariable Long schemeId) {

		byte[] pdf = this.schemePdfService.generateCompleteSchemePdf(schemeId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=CAMPA_Scheme_" + schemeId + ".pdf")
				.body(pdf);
	}
}