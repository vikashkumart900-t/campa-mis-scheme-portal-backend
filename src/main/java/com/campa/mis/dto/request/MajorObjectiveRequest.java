package com.campa.mis.dto.request;

import java.util.List;

public record MajorObjectiveRequest(
	    String majorObjective,
	    List<String> minorObjectives
	) {}