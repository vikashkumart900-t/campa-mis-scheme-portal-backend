package com.campa.mis.dto.response;

import java.util.List;

public record SchemeSupportingResponse(
        Long schemeId,
        String capacityBuildingDetails,
        List<SchemeSupportingDocumentResponse> documents,
        List<SchemeEmploymentGenerationResponse> employmentDetails
) {}