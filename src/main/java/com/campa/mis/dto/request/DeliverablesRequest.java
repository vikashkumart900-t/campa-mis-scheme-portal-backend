package com.campa.mis.dto.request;

import java.util.List;

public record DeliverablesRequest(

    String expenditureType,

    List<DeliverableRequest> deliverables,

    List<YearlyExpenditureRequest> yearlyExpenditures,

    List<String> nationalImportance,

    List<String> internationalFootprints

) {}