package com.campa.mis.serviceImpl;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.ProposalTechnologyExitRequest;
import com.campa.mis.dto.response.ProposalTechnologyExitResponse;
import com.campa.mis.entity.ProposalTechnologyExitEntity;
import com.campa.mis.repo.ProposalTechnologyExitRepository;
import com.campa.mis.service.ProposalTechnologyExitService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProposalTechnologyExitServiceImpl implements ProposalTechnologyExitService {

	private final ProposalTechnologyExitRepository repository;

	// CREATE
	@Override
	@Transactional
	public ProposalTechnologyExitResponse save(ProposalTechnologyExitRequest request,
			MultipartFile technologicalDocument, MultipartFile exitProtocolDocument) {

		validateFile(technologicalDocument);
		validateFile(exitProtocolDocument);

		ProposalTechnologyExitEntity entity = new ProposalTechnologyExitEntity();

		entity.setSchemeId(request.schemeId());

		// Section 13
		entity.setTechnologicalInterventionDescription(request.technologicalInterventionDescription());

		entity.setTechnologicalDocumentName(technologicalDocument.getOriginalFilename());

		entity.setTechnologicalDocumentType(technologicalDocument.getContentType());

		try {
			entity.setTechnologicalDocumentData(technologicalDocument.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Section 14
		entity.setExitProtocolDescription(request.exitProtocolDescription());

		entity.setExitProtocolDocumentName(exitProtocolDocument.getOriginalFilename());

		entity.setExitProtocolDocumentType(exitProtocolDocument.getContentType());

		try {
			entity.setExitProtocolDocumentData(exitProtocolDocument.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProposalTechnologyExitEntity savedEntity = repository.save(entity);

		return mapToResponse(savedEntity);
	}

	// FILE VALIDATION
	private void validateFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Document file is required");
		}

		if (file.getSize() > 20L * 1024 * 1024) {
			throw new IllegalArgumentException("File size must not exceed 20 MB");
		}
	}

	// ENTITY TO RESPONSE DTO
	private ProposalTechnologyExitResponse mapToResponse(ProposalTechnologyExitEntity technologyExitEntity) {

		return new ProposalTechnologyExitResponse(technologyExitEntity.getId(), technologyExitEntity.getSchemeId(),

				technologyExitEntity.getTechnologicalInterventionDescription(),
				technologyExitEntity.getTechnologicalDocumentName(),
				technologyExitEntity.getTechnologicalDocumentType(),

				technologyExitEntity.getExitProtocolDescription(), technologyExitEntity.getExitProtocolDocumentName(),
				technologyExitEntity.getExitProtocolDocumentType(),

				technologyExitEntity.getCreatedAt(), technologyExitEntity.getUpdatedAt());
	}
}
