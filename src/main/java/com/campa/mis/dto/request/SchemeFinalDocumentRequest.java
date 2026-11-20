package com.campa.mis.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record SchemeFinalDocumentRequest(

		MultipartFile signatureFile,

		MultipartFile supportingDocument

) {
}