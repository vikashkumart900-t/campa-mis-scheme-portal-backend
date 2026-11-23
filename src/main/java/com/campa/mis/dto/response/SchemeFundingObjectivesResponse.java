package com.campa.mis.dto.response;

import java.util.List;

public record SchemeFundingObjectivesResponse(
        StateFundingResponse stateFunding,
        List<SchemeObjectivesResponse> objectives
) {}