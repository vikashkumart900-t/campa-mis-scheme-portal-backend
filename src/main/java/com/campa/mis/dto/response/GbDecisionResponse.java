package com.campa.mis.dto.response;

import java.time.LocalDate;
import java.util.List;

public record GbDecisionResponse(
    Long gbDecisionId,
    Long schemeId,
    LocalDate meetingDate,
    String agendaItemNo,
    String agendaSubject,
    String gbDecision,
    String gbRemarks,
    String meetingMinutesFileName,
    String meetingMinutesContentType,
    String decisionStatus,
    List<GbStateDecisionResponse> stateDecisions
) {}