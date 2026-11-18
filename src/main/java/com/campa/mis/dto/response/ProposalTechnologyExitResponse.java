package com.campa.mis.dto.response;

import java.time.LocalDateTime;

public record ProposalTechnologyExitResponse(Long id, Long schemeId,

		String technologicalInterventionDescription, String technologicalDocumentName, String technologicalDocumentType,

		String exitProtocolDescription, String exitProtocolDocumentName, String exitProtocolDocumentType,

		LocalDateTime createdAt, LocalDateTime updatedAt) {

}
