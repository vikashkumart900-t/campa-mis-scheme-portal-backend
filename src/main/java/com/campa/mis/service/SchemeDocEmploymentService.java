package com.campa.mis.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.SchemeSupportingRequest;

public interface SchemeDocEmploymentService {

	SchemeSupportingRequest saveDocEmploymentDetails(Long schemeId, SchemeSupportingRequest request,
			List<MultipartFile> kmlFiles, List<MultipartFile> pngFiles) throws IOException;

}