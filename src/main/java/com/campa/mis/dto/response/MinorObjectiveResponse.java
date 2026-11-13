package com.campa.mis.dto.response;

public record MinorObjectiveResponse(
	    Long minorObjectiveId,
	    String minorObjective,
	    Integer displayOrder
	) {}