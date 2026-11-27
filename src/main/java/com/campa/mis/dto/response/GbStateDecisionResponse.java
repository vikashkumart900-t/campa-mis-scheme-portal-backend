package com.campa.mis.dto.response;

public record GbStateDecisionResponse(
    Long gbStateDecisionId,
    String stateCode,
    String stateName,
    Boolean isSelectedForStateApo,
    String stateGbDecision
) {}