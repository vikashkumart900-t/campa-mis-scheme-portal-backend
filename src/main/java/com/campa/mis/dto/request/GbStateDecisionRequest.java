package com.campa.mis.dto.request;

import jakarta.validation.constraints.NotNull;

public record GbStateDecisionRequest(

		@NotNull String stateCode,

		Boolean isSelectedForStateApo) {
}