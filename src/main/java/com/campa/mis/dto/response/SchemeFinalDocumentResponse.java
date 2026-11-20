package com.campa.mis.dto.response;

import java.time.LocalDateTime;

public record SchemeFinalDocumentResponse(

		Long documentId, Long schemeId,

		String signatureFileName, byte[] signatureFileData,

		String supportingDocumentName, byte[] supportingDocumentData,

		LocalDateTime uploadedAt

) {}