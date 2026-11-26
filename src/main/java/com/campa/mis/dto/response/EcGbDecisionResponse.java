package com.campa.mis.dto.response;

public record EcGbDecisionResponse(

    String message,

    EcDecisionResponse ecDecision,

    GbDecisionResponse gbDecision,

    boolean gbEligible

) {}