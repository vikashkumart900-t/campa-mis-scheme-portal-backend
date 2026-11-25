package com.campa.mis.serviceImpl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.ProposalTechnologyExitRequest;
import com.campa.mis.dto.response.ProposalTechnologyExitResponse;
import com.campa.mis.entity.SchemeTechnologyExitEntity;
import com.campa.mis.repo.ProposalTechnologyExitRepository;
import com.campa.mis.service.ProposalTechnologyExitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProposalTechnologyExitServiceImpl implements ProposalTechnologyExitService {

	private final ProposalTechnologyExitRepository technologyExitRepository;

	@Override
	@Transactional
	public ProposalTechnologyExitResponse saveProposalTechExit(Long schemeId, ProposalTechnologyExitRequest request,
			MultipartFile technologicalDocument, MultipartFile exitProtocolDocument) {

		validateFile(technologicalDocument);
		validateFile(exitProtocolDocument);

		SchemeTechnologyExitEntity technologyExitEntity = this.technologyExitRepository.findBySchemeId(schemeId)
				.orElseGet(SchemeTechnologyExitEntity::new);

		technologyExitEntity.setSchemeId(schemeId);

		// Section 13
		technologyExitEntity.setTechnologicalInterventionDescription(request.technologicalInterventionDescription());

		technologyExitEntity.setTechnologicalDocumentName(technologicalDocument.getOriginalFilename());

		technologyExitEntity.setTechnologicalDocumentType(technologicalDocument.getContentType());

		try {
			technologyExitEntity.setTechnologicalDocumentData(technologicalDocument.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Section 14
		technologyExitEntity.setExitProtocolDescription(request.exitProtocolDescription());

		technologyExitEntity.setExitProtocolDocumentName(exitProtocolDocument.getOriginalFilename());

		technologyExitEntity.setExitProtocolDocumentType(exitProtocolDocument.getContentType());

		try {
			technologyExitEntity.setExitProtocolDocumentData(exitProtocolDocument.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SchemeTechnologyExitEntity savedEntity = this.technologyExitRepository.save(technologyExitEntity);

		return mapToResponse(savedEntity, schemeId);
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
	private ProposalTechnologyExitResponse mapToResponse(SchemeTechnologyExitEntity technologyExitEntity,
			Long schemeId) {

		return new ProposalTechnologyExitResponse(technologyExitEntity.getId(), schemeId,

				technologyExitEntity.getTechnologicalInterventionDescription(),
				technologyExitEntity.getTechnologicalDocumentName(),
				technologyExitEntity.getTechnologicalDocumentType(),

				technologyExitEntity.getExitProtocolDescription(), technologyExitEntity.getExitProtocolDocumentName(),
				technologyExitEntity.getExitProtocolDocumentType(),

				technologyExitEntity.getCreatedAt(), technologyExitEntity.getUpdatedAt(),
				technologyExitEntity.getTechnologicalDocumentData(),
				technologyExitEntity.getExitProtocolDocumentData());
	}

	@Override
	@Transactional(readOnly = true)
	public ProposalTechnologyExitResponse getProposalTechExitBySchemeId(Long schemeId) {

		SchemeTechnologyExitEntity technologyExitEntity = technologyExitRepository.findBySchemeId(schemeId).orElseThrow(
				() -> new RuntimeException("Technology exit details not found for scheme ID: " + schemeId));

		return mapToResponse(technologyExitEntity, schemeId);
	}
}
