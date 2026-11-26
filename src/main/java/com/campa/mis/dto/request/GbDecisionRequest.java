package com.campa.mis.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GbDecisionRequest(

    @NotNull
    LocalDate meetingDate,

    @NotBlank
    String agendaItemNo,

    @NotBlank
    @Size(max = 500)
    String agendaSubject,

    @NotBlank
    @Pattern(regexp = "APPROVED|RETURNED")
    String gbDecision,

    @NotBlank
    @Size(max = 1000)
    String gbRemarks,
//
//    @NotBlank
//    @Pattern(regexp = "PENDING|COMPLETED")
//    String decisionStatus,

    @NotNull
    @Valid
    List<GbStateDecisionRequest> stateDecisions
) {}