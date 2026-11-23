package com.campa.mis.dto.response;

import java.util.List;

public record StateDistrictResponse(
    String stateName,
    List<String> districts
) {}