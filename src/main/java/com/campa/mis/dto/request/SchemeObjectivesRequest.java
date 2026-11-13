package com.campa.mis.dto.request;

import java.util.List;

public record SchemeObjectivesRequest(
	    List<MajorObjectiveRequest> majorObjectives
	) {}