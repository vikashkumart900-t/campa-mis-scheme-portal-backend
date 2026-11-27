package com.campa.mis.dto.response;

import java.util.List;

public record BulkStateHodEmailResponse(

        Boolean success,
        String message,
        Long schemeId,
        Integer totalStates,
        Integer successCount,
        Integer failedCount,
        List<StateEmailResult> results

) {

    public record StateEmailResult(

            String stateCode,
            String stateName,
            String email,
            String status,
            String message

    ) {}
}