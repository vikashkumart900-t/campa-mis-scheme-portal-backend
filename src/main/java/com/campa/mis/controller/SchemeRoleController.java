package com.campa.mis.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.campa.mis.dto.request.SchemeRoleStatusUpdateRequest;
import com.campa.mis.entity.SchemeRoleStatusEntity;
import com.campa.mis.service.SchemeFinalDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mis")
@RequiredArgsConstructor
public class SchemeRoleController {

	private final SchemeFinalDocumentService finalDocumentService;

	@GetMapping("/schemeRoleStatusByID/{schemeId}")
	public ResponseEntity<SchemeRoleStatusEntity> getSchemeRoleStatusById(@PathVariable Long schemeId) {

		return ResponseEntity.ok(this.finalDocumentService.getSchemeRoleStatusById(schemeId));
	}

	@PostMapping("/updateRoleStatus")
	public ResponseEntity<SchemeRoleStatusEntity> updateSchemeRoleStatus(
			@Valid @RequestBody SchemeRoleStatusUpdateRequest request) {

		SchemeRoleStatusEntity response = this.finalDocumentService.updateSchemeRoleStatus(request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/schemeRoleStatus")
	public ResponseEntity<List<SchemeRoleStatusEntity>> getSchemeRoleStatus() {

		return ResponseEntity.ok(this.finalDocumentService.getSchemeRoleStatus());
	}
	
	

}
