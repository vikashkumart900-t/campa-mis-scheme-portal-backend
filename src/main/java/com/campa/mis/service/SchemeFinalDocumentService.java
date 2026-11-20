package com.campa.mis.service;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.response.SchemeFinalDocumentResponse;

public interface SchemeFinalDocumentService {

	SchemeFinalDocumentResponse saveFinalDocuments(Long schemeId, MultipartFile signatureFile,
			MultipartFile supportingDocument);

	SchemeFinalDocumentResponse getFinalDocuments(Long schemeId);
}