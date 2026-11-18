package com.campa.mis.dto.response;

public record StateFundingResponse(

		Long id,

		Long schemeId,

		Boolean hasStateComponents,

		Boolean stateOnboard,

		String stateAgreementFileName,

		Boolean hasOtherFundingAgency,

		String otherFundingSources,

		String fundingDocumentFileName

) {

}
