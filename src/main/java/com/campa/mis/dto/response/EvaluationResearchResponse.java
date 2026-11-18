package com.campa.mis.dto.response;

import java.util.List;

import com.campa.mis.enums.EvaluationType;

public record EvaluationResearchResponse(

    Long id,

    Long schemeId,

    EvaluationType evaluationType,

    List<String> evaluationDetails,

    String documentFileName,

    String documentContentType,

    String researchAspects,

    String uniqueHighlights

) {}