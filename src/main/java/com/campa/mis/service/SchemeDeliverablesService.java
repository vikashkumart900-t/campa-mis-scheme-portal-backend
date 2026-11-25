package com.campa.mis.service;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.dto.response.DeliverablesResponse;

public interface SchemeDeliverablesService {

	DeliverablesResponse saveDeliverables(Long schemeId, DeliverablesRequest request,
			MultipartFile multipartRequest);

	DeliverablesResponse getDeliverables(Long schemeId);

}