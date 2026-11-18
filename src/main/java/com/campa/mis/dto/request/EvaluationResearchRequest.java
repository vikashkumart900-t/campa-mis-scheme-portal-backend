package com.campa.mis.dto.request;

import java.util.List;

import com.campa.mis.enums.EvaluationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EvaluationResearchRequest(

    @NotNull(message = "Please select evaluation type")
    EvaluationType evaluationType,

    @NotEmpty(message = "Evaluation details are required")
    List<@NotBlank(message = "Evaluation detail cannot be blank") String> evaluationDetails,

    @NotBlank(message = "Research aspects are required")
    String researchAspects,

    String uniqueHighlights

) {}