package com.campa.mis.dto.response;

public record StateHodRegistrationResponse(
    Long id,
    Long schemeId,
    String stateCode,
    String name,
    String email,
    String mobileNumber,
    String designation,
    boolean status,
    String message
) {}