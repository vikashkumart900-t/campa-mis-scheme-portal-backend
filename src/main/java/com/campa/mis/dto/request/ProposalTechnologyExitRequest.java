package com.campa.mis.dto.request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProposalTechnologyExitRequest(

		@NotNull Long schemeId,

		// Section 13
		@NotBlank String technologicalInterventionDescription,

		@NotNull MultipartFile technologicalDocument,

		// Section 14
		@NotBlank String exitProtocolDescription,

		@NotNull MultipartFile exitProtocolDocument

) {
}
