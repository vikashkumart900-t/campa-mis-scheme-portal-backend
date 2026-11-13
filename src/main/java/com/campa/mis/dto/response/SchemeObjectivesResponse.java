package com.campa.mis.dto.response;

import java.util.List;

public record SchemeObjectivesResponse(
	    Long objectiveId,
	    String majorObjective,
	    int displayOrder,
	    List<MinorObjectiveResponse> minorObjectives
	) {}