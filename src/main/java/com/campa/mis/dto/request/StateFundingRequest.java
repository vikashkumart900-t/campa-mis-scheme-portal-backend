package com.campa.mis.dto.request;

public record StateFundingRequest(

    Boolean hasStateComponents,

    Boolean stateOnboard,

    String stateAgreementFileName,

    String stateAgreementFilePath,

    Boolean hasOtherFundingAgency,

    String otherFundingSources,

    String fundingDocumentFileName,

    String fundingDocumentFilePath

) {}