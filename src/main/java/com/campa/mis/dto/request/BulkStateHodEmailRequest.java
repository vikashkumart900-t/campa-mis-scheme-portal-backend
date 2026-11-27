package com.campa.mis.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BulkStateHodEmailRequest(

    @NotNull
    Long schemeId,

    @NotEmpty
    List<String> stateCodes

) {}