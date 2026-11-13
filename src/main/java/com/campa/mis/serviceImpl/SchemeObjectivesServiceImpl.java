package com.campa.mis.serviceImpl;

import com.campa.mis.dto.request.MajorObjectiveRequest;
import com.campa.mis.dto.request.SchemeObjectivesRequest;
import com.campa.mis.dto.request.StateFundingRequest;
import com.campa.mis.dto.response.DocumentUploadResponse;
import com.campa.mis.dto.response.MinorObjectiveResponse;
import com.campa.mis.dto.response.SchemeObjectivesResponse;
import com.campa.mis.dto.response.StateFundingResponse;
import com.campa.mis.entity.*;
import com.campa.mis.repo.*;
import com.campa.mis.service.SchemeObjectivesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchemeObjectivesServiceImpl implements SchemeObjectivesService {

	private final SchemeObjectiveRepository objectiveRepository;
	private final SchemeMinorObjectiveRepository minorRepository;
	private final SchemeStateFundingRepository fundingRepository;

	private static final String UPLOAD_DIR = "uploads/schemes/";

	// SAVE OBJECTIVES
	@Override
	@Transactional
	public List<SchemeObjectivesResponse> saveObjectives(Long schemeId, SchemeObjectivesRequest request) {

		if (request.majorObjectives() == null || request.majorObjectives().isEmpty()) {
			throw new IllegalArgumentException("At least one major objective is required");
		}

		// Remove existing objectives.
		// Minor objectives are removed automatically through FK cascade.
		//objectiveRepository.deleteBySchemeId(schemeId);

		List<SchemeObjectivesResponse> responses = new ArrayList<>();

		int majorOrder = 1;

		for (MajorObjectiveRequest major : request.majorObjectives()) {

			if (major.majorObjective() == null || major.majorObjective().isBlank()) {
				throw new IllegalArgumentException("Major objective cannot be empty");
			}

			SchemeObjectiveEntity objective = new SchemeObjectiveEntity();

			objective.setSchemeId(schemeId);
			objective.setMajorObjective(major.majorObjective().trim());
			objective.setDisplayOrder(majorOrder++);

			SchemeObjectiveEntity savedObjective = objectiveRepository.save(objective);

			List<MinorObjectiveResponse> minorResponses = new ArrayList<>();

			if (major.minorObjectives() != null) {

				int minorOrder = 1;

				for (String minor : major.minorObjectives()) {

					if (minor == null || minor.isBlank()) {
						continue;
					}

					SchemeMinorObjectiveEntity minorEntity = new SchemeMinorObjectiveEntity();

					minorEntity.setObjectiveId(savedObjective.getObjectiveId());

					minorEntity.setMinorObjective(minor.trim());
					minorEntity.setDisplayOrder(minorOrder++);

					SchemeMinorObjectiveEntity savedMinor = minorRepository.save(minorEntity);

					minorResponses.add(new MinorObjectiveResponse(savedMinor.getMinorObjectiveId(),
							savedMinor.getMinorObjective(), savedMinor.getDisplayOrder()));
				}
			}

			responses.add(new SchemeObjectivesResponse(savedObjective.getObjectiveId(),
					savedObjective.getMajorObjective(), savedObjective.getDisplayOrder(), minorResponses));
		}

		return responses;
	}

	// FETCH OBJECTIVES
	@Override
	@Transactional(readOnly = true)
	public List<SchemeObjectivesResponse> getObjectives(Long schemeId) {

		List<SchemeObjectiveEntity> objectives = objectiveRepository.findBySchemeIdOrderByDisplayOrderAsc(schemeId);

		List<SchemeObjectivesResponse> responses = new ArrayList<>();

		for (SchemeObjectiveEntity objective : objectives) {

			List<MinorObjectiveResponse> minors = minorRepository
					.findByObjectiveIdOrderByDisplayOrderAsc(objective.getObjectiveId()).stream()
					.map(m -> new MinorObjectiveResponse(m.getMinorObjectiveId(), m.getMinorObjective(),
							m.getDisplayOrder()))
					.toList();

			responses.add(new SchemeObjectivesResponse(objective.getObjectiveId(), objective.getMajorObjective(),
					objective.getDisplayOrder(), minors));
		}

		return responses;
	}

	// SAVE OR UPDATE STATE FUNDING DETAILS
	@Override
	@Transactional
	public StateFundingResponse saveStateFunding(Long schemeId, StateFundingRequest request) {

		if (request.hasStateComponents() == null || request.hasOtherFundingAgency() == null) {
			throw new IllegalArgumentException("Please select Yes or No for both sections");
		}

		if (Boolean.TRUE.equals(request.hasStateComponents()) && request.stateOnboard() == null) {
			throw new IllegalArgumentException("Please select whether the state is onboard");
		}

		if (Boolean.TRUE.equals(request.hasOtherFundingAgency())
				&& (request.otherFundingSources() == null || request.otherFundingSources().isBlank())) {
			throw new IllegalArgumentException("Please specify other funding sources");
		}

		SchemeStateFundingEntity schemeStateFundingEntity = fundingRepository.findBySchemeId(schemeId)
				.orElseGet(SchemeStateFundingEntity::new);

		schemeStateFundingEntity.setSchemeId(schemeId);

		schemeStateFundingEntity.setHasStateComponents(request.hasStateComponents());

		schemeStateFundingEntity.setStateOnboard(Boolean.TRUE.equals(request.hasStateComponents()) ? request.stateOnboard() : null);

		schemeStateFundingEntity.setStateAgreementFileName(
				Boolean.TRUE.equals(request.hasStateComponents()) ? request.stateAgreementFileName() : null);

		schemeStateFundingEntity.setStateAgreementFilePath(
				Boolean.TRUE.equals(request.hasStateComponents()) ? request.stateAgreementFilePath() : null);

		schemeStateFundingEntity.setHasOtherFundingAgency(request.hasOtherFundingAgency());

		schemeStateFundingEntity.setOtherFundingSources(
				Boolean.TRUE.equals(request.hasOtherFundingAgency()) ? request.otherFundingSources() : null);

		schemeStateFundingEntity.setFundingDocumentFileName(
				Boolean.TRUE.equals(request.hasOtherFundingAgency()) ? request.fundingDocumentFileName() : null);

		schemeStateFundingEntity.setFundingDocumentFilePath(
				Boolean.TRUE.equals(request.hasOtherFundingAgency()) ? request.fundingDocumentFilePath() : null);

		SchemeStateFundingEntity saved = fundingRepository.save(schemeStateFundingEntity);

		return mapFundingResponse(saved);
	}

	// FETCH STATE FUNDING DETAILS
	@Override
	@Transactional(readOnly = true)
	public StateFundingResponse getStateFunding(Long schemeId) {

		SchemeStateFundingEntity entity = fundingRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new RuntimeException("State funding details not found"));

		return mapFundingResponse(entity);
	}

	private StateFundingResponse mapFundingResponse(SchemeStateFundingEntity entity) {

		return new StateFundingResponse(entity.getId(), entity.getSchemeId(), entity.getHasStateComponents(),
				entity.getStateOnboard(), entity.getStateAgreementFileName(), entity.getStateAgreementFilePath(),
				entity.getHasOtherFundingAgency(), entity.getOtherFundingSources(), entity.getFundingDocumentFileName(),
				entity.getFundingDocumentFilePath());
	}

	// UPLOAD DOCUMENT
	@Override
	public DocumentUploadResponse uploadDocument(Long schemeId, String documentType, MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Please select a file");
		}

		Set<String> allowedTypes = Set.of("STATE_AGREEMENT", "FUNDING_DOCUMENT");

		if (!allowedTypes.contains(documentType)) {
			throw new IllegalArgumentException("Invalid document type");
		}

		String originalName = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

		String extension = originalName.contains(".")
				? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
				: "";

		if (!Set.of(".pdf", ".doc", ".docx").contains(extension)) {
			throw new IllegalArgumentException("Only PDF, DOC and DOCX files are allowed");
		}

		try {
			Path directory = Paths.get(UPLOAD_DIR + schemeId).toAbsolutePath().normalize();

			Files.createDirectories(directory);

			String storedName = UUID.randomUUID() + extension;

			Path target = directory.resolve(storedName).normalize();

			if (!target.startsWith(directory)) {
				throw new IllegalArgumentException("Invalid file path");
			}

			Files.copy(file.getInputStream(), target);

			return new DocumentUploadResponse(originalName, target.toString(), "File uploaded successfully");

		} catch (IOException e) {
			throw new RuntimeException("Document upload failed", e);
		}
	}

	@Override
	@Transactional
	public StateFundingResponse saveStateFunding(Long schemeId, Boolean hasStateComponents, Boolean stateOnboard,
			MultipartFile stateAgreementFile, Boolean hasOtherFundingAgency, String otherFundingSources,
			MultipartFile fundingDocumentFile) throws IOException {

		SchemeStateFundingEntity schemeStateFundingEntity = fundingRepository.findBySchemeId(schemeId)
				.orElseGet(SchemeStateFundingEntity::new);

		schemeStateFundingEntity.setSchemeId(schemeId);

		schemeStateFundingEntity.setHasStateComponents(hasStateComponents);

		schemeStateFundingEntity.setStateOnboard(Boolean.TRUE.equals(hasStateComponents) ? stateOnboard : null);

		if (Boolean.TRUE.equals(hasStateComponents) && stateAgreementFile != null && !stateAgreementFile.isEmpty()) {

			schemeStateFundingEntity.setStateAgreementFileName(stateAgreementFile.getOriginalFilename());

			schemeStateFundingEntity.setStateAgreementFile(stateAgreementFile.getBytes());

		} else if (!Boolean.TRUE.equals(hasStateComponents)) {

			schemeStateFundingEntity.setStateAgreementFileName(null);
			schemeStateFundingEntity.setStateAgreementFile(null);
		}

		schemeStateFundingEntity.setHasOtherFundingAgency(hasOtherFundingAgency);

		schemeStateFundingEntity
				.setOtherFundingSources(Boolean.TRUE.equals(hasOtherFundingAgency) ? otherFundingSources : null);

		if (Boolean.TRUE.equals(hasOtherFundingAgency) && fundingDocumentFile != null
				&& !fundingDocumentFile.isEmpty()) {

			schemeStateFundingEntity.setFundingDocumentFileName(fundingDocumentFile.getOriginalFilename());

			schemeStateFundingEntity.setFundingDocumentFile(fundingDocumentFile.getBytes());

		} else if (!Boolean.TRUE.equals(hasOtherFundingAgency)) {

			schemeStateFundingEntity.setFundingDocumentFileName(null);
			schemeStateFundingEntity.setFundingDocumentFile(null);
		}

		SchemeStateFundingEntity saved = fundingRepository.save(schemeStateFundingEntity);

		return mapFundingResponse(saved);
	}
}