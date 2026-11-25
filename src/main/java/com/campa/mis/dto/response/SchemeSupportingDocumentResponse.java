package com.campa.mis.dto.response;

public record SchemeSupportingDocumentResponse(
        Long id,
        String documentType,
        String description,
        String fileName,
        String contentType,
        byte[] supportingDocumentData
) {}