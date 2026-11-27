package com.campa.mis.dto.response;

public record StateHodAccountResponse(
    Long id,
    Long schemeId,
    String stateCode,
    String name,
    String email,
    String mobileNumber,
    String designation,
    String emailStatus
) {}