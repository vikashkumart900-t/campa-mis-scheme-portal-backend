
package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.campa.mis.dto.request.SchemeRoleStatusUpdateRequest;
import com.campa.mis.dto.response.SchemeFinalDocumentResponse;
import com.campa.mis.entity.SchemeFinalDocumentEntity;
import com.campa.mis.entity.SchemeRoleStatusEntity;
import com.campa.mis.repo.SchemeFinalDocumentRepository;
import com.campa.mis.repo.SchemeRoleStatusRepository;
import com.campa.mis.service.SchemeFinalDocumentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeFinalDocumentServiceImpl implements SchemeFinalDocumentService {

	private final SchemeFinalDocumentRepository finalDocumentRepository;
	private final SchemeRoleStatusRepository schemeRoleStatusRepository;

	// POST: SAVE / UPDATE DOCUMENTS
	@Override
	@Transactional
	public SchemeFinalDocumentResponse saveFinalDocuments(Long schemeId, MultipartFile signatureFile,
			MultipartFile supportingDocument) {

		if (schemeId == null || schemeId <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scheme ID");
		}

		SchemeFinalDocumentEntity document = this.finalDocumentRepository.findBySchemeId(schemeId)
				.orElseGet(SchemeFinalDocumentEntity::new);

		// Signature mandatory for new record
		if (document.getDocumentId() == null && (signatureFile == null || signatureFile.isEmpty())) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signature file is mandatory");
		}

		try {

			document.setSchemeId(schemeId);

			// Save / Update Signature
			if (signatureFile != null && !signatureFile.isEmpty()) {

				document.setSignatureFileName(signatureFile.getOriginalFilename());

				document.setSignatureFileData(signatureFile.getBytes());
			}

			// Save / Update Supporting Document
			if (supportingDocument != null && !supportingDocument.isEmpty()) {

				document.setSupportingDocumentName(supportingDocument.getOriginalFilename());

				document.setSupportingDocumentData(supportingDocument.getBytes());
			}

			SchemeFinalDocumentEntity saved = this.finalDocumentRepository.save(document);

			// Save / Update Scheme Role Status
			SchemeRoleStatusEntity roleStatus = schemeRoleStatusRepository.findBySchemeId(schemeId)
					.orElseGet(() -> SchemeRoleStatusEntity.builder().schemeId(schemeId).hodStatus("PENDING")
							.pdStatus("PENDING").campaStatus("PENDING").createdAt(LocalDateTime.now()).build());

			schemeRoleStatusRepository.save(roleStatus);

			return mapResponse(saved);

		} catch (IOException e) {

			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read uploaded document", e);
		}
	}

	// GET: FETCH DOCUMENTS
	@Override
	@Transactional(readOnly = true)
	public SchemeFinalDocumentResponse getFinalDocuments(Long schemeId) {

		if (schemeId == null || schemeId <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scheme ID");
		}

		SchemeFinalDocumentEntity document = this.finalDocumentRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Final documents not found for scheme ID: " + schemeId));

		return mapResponse(document);
	}

	// ENTITY TO RESPONSE DTO
	private SchemeFinalDocumentResponse mapResponse(SchemeFinalDocumentEntity document) {

		return new SchemeFinalDocumentResponse(document.getDocumentId(), document.getSchemeId(),
				document.getSignatureFileName(), document.getSignatureFileData(), document.getSupportingDocumentName(),
				document.getSupportingDocumentData(), document.getUploadedAt());
	}

	@Override
	public SchemeRoleStatusEntity getSchemeRoleStatusById(Long schemeId) {

		return this.schemeRoleStatusRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Scheme role status not found for scheme ID: " + schemeId));
	}

	@Override
	@Transactional
	public SchemeRoleStatusEntity updateSchemeRoleStatus(SchemeRoleStatusUpdateRequest request) {

		SchemeRoleStatusEntity roleStatusEntity = schemeRoleStatusRepository.findBySchemeId(request.schemeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scheme role status not found"));

		if (request.status() == null || request.roleCode() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role code and status are required");
		}

		String status = request.status().toUpperCase().trim();
		String roleCode = request.roleCode().toUpperCase().trim();

		if (!Set.of("APPROVED", "REVERTED").contains(status)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
		}

		// Sequential approval validation
		switch (roleCode) {

		case "HOD":
			// HOD is the first approval level
			roleStatusEntity.setHodStatus(status);
			roleStatusEntity.setHodRemarks(request.hodRemarks());
			roleStatusEntity.setHodUpdatedAt(LocalDateTime.now());
			break;

		case "PD":

			// HOD must approve first
			if (!"APPROVED".equalsIgnoreCase(roleStatusEntity.getHodStatus())) {

				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "PD can take action only after HOD approval");
			}

			roleStatusEntity.setPdStatus(status);
			roleStatusEntity.setPdRemarks(request.pdRemarks());
			roleStatusEntity.setPdUpdatedAt(LocalDateTime.now());
			break;

		case "NATIONAL_CAMPA":

			// HOD must approve
			if (!"APPROVED".equalsIgnoreCase(roleStatusEntity.getHodStatus())
					&& !"APPROVED".equalsIgnoreCase(roleStatusEntity.getPdStatus())) {

				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
						"CAMPA can take action only after HOD and PD approval");
			}

			roleStatusEntity.setCampaStatus(status);
			roleStatusEntity.setCampaRemarks(request.campaRemarks());
			roleStatusEntity.setCampaUpdatedAt(LocalDateTime.now());
			break;

		default:
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role code");
		}

		return schemeRoleStatusRepository.save(roleStatusEntity);
	}

	@Override
	public List<SchemeRoleStatusEntity> getSchemeRoleStatus() {
		return this.schemeRoleStatusRepository.findAll();
	}

}
