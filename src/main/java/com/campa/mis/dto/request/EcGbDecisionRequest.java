package com.campa.mis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EcGbDecisionRequest(

    @NotNull(message = "EC decision is required")
    @Valid
    EcDecisionRequest ecDecision,

    // Optional because EC may be RETURNED
    @Valid
    GbDecisionRequest gbDecision

) {}