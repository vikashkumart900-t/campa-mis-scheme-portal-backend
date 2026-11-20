
package com.campa.mis.serviceImpl;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.campa.mis.dto.response.SchemeFinalDocumentResponse;
import com.campa.mis.entity.SchemeFinalDocumentEntity;
import com.campa.mis.repo.SchemeFinalDocumentRepository;
import com.campa.mis.service.SchemeFinalDocumentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeFinalDocumentServiceImpl implements SchemeFinalDocumentService {

	private final SchemeFinalDocumentRepository finalDocumentRepository;

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
}
