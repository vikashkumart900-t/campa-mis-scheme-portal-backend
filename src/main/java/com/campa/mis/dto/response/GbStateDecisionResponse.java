package com.campa.mis.dto.response;

public record GbStateDecisionResponse(
    Long gbStateDecisionId,
    Long stateCode,
    Boolean isSelectedForStateApo,
    String stateGbDecision
) {}