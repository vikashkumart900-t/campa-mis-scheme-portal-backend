package com.campa.mis.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GbStateDecisionRequest(

		@NotNull Long stateCode,

		Boolean isSelectedForStateApo,

		@NotNull @Pattern(regexp = "APPROVED|NOT_APPROVED|PENDING") String stateGbDecision) {
}