package com.campa.mis.dto.response;

public record SchemeDetailsResponse(
        String schemeName,
        String organizationName,
        String divisionName,
        Long organizationId,
        Long divisionId,
        Long schemeId
) {
}