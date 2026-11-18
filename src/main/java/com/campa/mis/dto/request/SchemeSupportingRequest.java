package com.campa.mis.dto.request;

import java.util.List;

public record SchemeSupportingRequest(

    String capacityBuildingDetails,

    List<SupportingDocumentRequest> documents,

    List<EmploymentGenerationRequest> employmentDetails

) {}