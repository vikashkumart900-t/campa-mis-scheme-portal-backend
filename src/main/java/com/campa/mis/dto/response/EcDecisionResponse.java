package com.campa.mis.dto.response;

import java.time.LocalDate;

public record EcDecisionResponse(
    Long ecDecisionId,
    Long schemeId,
    LocalDate meetingDate,
    String agendaItemNo,
    String agendaSubject,
    String ecDecision,
    String ecRemarks,
    String meetingMinutesFileName,
    String meetingMinutesContentType,
    String decisionStatus
) {}