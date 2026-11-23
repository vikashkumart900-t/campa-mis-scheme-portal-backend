package com.campa.mis.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.SchemeRoleStatusUpdateRequest;
import com.campa.mis.dto.response.SchemeFinalDocumentResponse;
import com.campa.mis.entity.SchemeRoleStatusEntity;

public interface SchemeFinalDocumentService {

	SchemeFinalDocumentResponse saveFinalDocuments(Long schemeId, MultipartFile signatureFile,
			MultipartFile supportingDocument);

	SchemeFinalDocumentResponse getFinalDocuments(Long schemeId);

	SchemeRoleStatusEntity getSchemeRoleStatusById(Long schemeId);

	List<SchemeRoleStatusEntity> getSchemeRoleStatus();

	SchemeRoleStatusEntity updateSchemeRoleStatus(SchemeRoleStatusUpdateRequest request);
}