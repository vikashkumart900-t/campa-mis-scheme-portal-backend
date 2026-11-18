package com.campa.mis.service;

import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.dto.response.DeliverablesResponse;

public interface SchemeDeliverablesService {

	DeliverablesResponse saveDeliverables(Long schemeId, DeliverablesRequest request);

	DeliverablesResponse getDeliverables(Long schemeId);

	DeliverablesResponse updateDeliverables(Long schemeId, DeliverablesRequest request);
}