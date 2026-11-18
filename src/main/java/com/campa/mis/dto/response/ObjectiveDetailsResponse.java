package com.campa.mis.dto.response;

import java.util.List;

public record ObjectiveDetailsResponse(

		List<SchemeObjectivesResponse> objectives,

		StateFundingResponse stateFunding

) {

}
