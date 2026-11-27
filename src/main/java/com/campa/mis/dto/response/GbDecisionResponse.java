package com.campa.mis.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    List<GbStateDecisionResponse> stateDecisions,
    LocalDateTime campaApprovalDate,
    String schemeName,
    String proposalID
) {}