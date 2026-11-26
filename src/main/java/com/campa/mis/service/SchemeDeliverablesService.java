package com.campa.mis.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.dto.response.DeliverablesResponse;

public interface SchemeDeliverablesService {

	DeliverablesResponse saveDeliverables(Long schemeId, DeliverablesRequest request,
			List<MultipartFile> multipartRequest);

	DeliverablesResponse getDeliverables(Long schemeId);

}