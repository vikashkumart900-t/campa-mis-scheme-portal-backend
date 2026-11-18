package com.campa.mis.dto.request;

import jakarta.validation.Valid;

public record ObjectiveDetailsRequest(

		@Valid SchemeObjectivesRequest objectives,

		@Valid StateFundingRequest stateFunding

) {
}
