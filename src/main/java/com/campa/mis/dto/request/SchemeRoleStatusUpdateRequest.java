package com.campa.mis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchemeRoleStatusUpdateRequest(

		@NotNull Long schemeId,

		@NotBlank String roleCode,

		@NotBlank String status,
		
		String hodRemarks,
		String pdRemarks,
		String campaRemarks

) {

}
