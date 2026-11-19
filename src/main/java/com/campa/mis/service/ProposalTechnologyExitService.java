package com.campa.mis.service;

import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.ProposalTechnologyExitRequest;
import com.campa.mis.dto.response.ProposalTechnologyExitResponse;
import io.jsonwebtoken.io.IOException;

public interface ProposalTechnologyExitService {

	ProposalTechnologyExitResponse saveProposalTechExit(ProposalTechnologyExitRequest request, MultipartFile technologicalDocument,
			MultipartFile exitProtocolDocument) throws IOException;

}
