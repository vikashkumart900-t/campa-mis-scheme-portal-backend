package com.campa.mis.controller;

import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.service.SchemeDeliverablesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeDeliverablesController {

	private final SchemeDeliverablesService schemeDeliverablesService;

	// SAVE
	@PostMapping("/{schemeId}/deliverables")
	public ResponseEntity<?> saveDeliverables(@PathVariable Long schemeId, @RequestBody DeliverablesRequest request) {

		return ResponseEntity.ok(this.schemeDeliverablesService.saveDeliverables(schemeId, request));
	}

	// FETCH
	@GetMapping("/{schemeId}/deliverables")
	public ResponseEntity<?> getDeliverables(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.schemeDeliverablesService.getDeliverables(schemeId));
	}

	// UPDATE
	@PutMapping("/{schemeId}/deliverables")
	public ResponseEntity<?> updateDeliverables(@PathVariable Long schemeId, @RequestBody DeliverablesRequest request) {

		return ResponseEntity.ok(this.schemeDeliverablesService.updateDeliverables(schemeId, request));
	}
}