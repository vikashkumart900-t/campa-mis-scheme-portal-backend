package com.campa.mis.controller;

import com.campa.mis.service.SchemePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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