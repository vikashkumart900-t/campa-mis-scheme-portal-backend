package com.campa.mis.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EcDecisionRequest(

    @NotNull
    LocalDate meetingDate,

    @NotBlank
    String agendaItemNo,

    @NotBlank
    @Size(max = 500)
    String agendaSubject,

    @NotBlank
    @Pattern(regexp = "APPROVED|RETURNED")
    String ecDecision,

    @NotBlank
    @Size(max = 1000)
    String ecRemarks

//    @NotBlank
//    @Pattern(regexp = "PENDING|COMPLETED")
//    String decisionStatus
) {}