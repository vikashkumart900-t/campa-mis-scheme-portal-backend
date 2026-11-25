package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.campa.mis.dto.request.MajorObjectiveRequest;
import com.campa.mis.dto.request.ObjectiveDetailsRequest;
import com.campa.mis.dto.request.SchemeObjectivesRequest;
import com.campa.mis.dto.request.StateFundingRequest;
import com.campa.mis.dto.response.DocumentUploadResponse;
import com.campa.mis.dto.response.MinorObjectiveResponse;
import com.campa.mis.dto.response.ObjectiveDetailsResponse;
import com.campa.mis.dto.response.SchemeFundingObjectivesResponse;
import com.campa.mis.dto.response.SchemeObjectivesResponse;
import com.campa.mis.dto.response.StateFundingResponse;
import com.campa.mis.entity.SchemeMinorObjectiveEntity;
import com.campa.mis.entity.SchemeObjectiveEntity;
import com.campa.mis.entity.SchemeStateFundingEntity;
import com.campa.mis.repo.SchemeMinorObjectiveRepository;
import com.campa.mis.repo.SchemeObjectiveRepository;
import com.campa.mis.repo.SchemeStateFundingRepository;
import com.campa.mis.service.SchemeObjectivesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeObjectivesServiceImpl implements SchemeObjectivesService {

	private final SchemeObjectiveRepository objectiveRepository;
	private final SchemeMinorObjectiveRepository minorRepository;
	private final SchemeStateFundingRepository fundingRepository;

	private static final String UPLOAD_DIR = "uploads/schemes/";

	@Transactional
	public ObjectiveDetailsResponse saveObjectivesDetails(Long schemeId, ObjectiveDetailsRequest request,
			MultipartFile stateAgreementFile, MultipartFile fundingDocumentFile) throws IOException {

		List<SchemeObjectivesResponse> objectives = saveObjectives(schemeId, request.objectives());

		StateFundingResponse funding = saveStateFunding(schemeId, request.stateFunding(), stateAgreementFile,
				fundingDocumentFile);

		return new ObjectiveDetailsResponse(objectives, funding);
	}

	// SAVE / UPDATE OBJECTIVES
	@Transactional
	public List<SchemeObjectivesResponse> saveObjectives(Long schemeId, SchemeObjectivesRequest request) {

		if (schemeId == null) {
			throw new IllegalArgumentException("Scheme ID is required");
		}

		if (request == null || request.majorObjectives() == null || request.majorObjectives().isEmpty()) {

			throw new IllegalArgumentException("At least one major objective is required");
		}

		// 1. FIND EXISTING OBJECTIVES
		List<SchemeObjectiveEntity> existingObjectives = objectiveRepository.findBySchemeId(schemeId);

		// 2. DELETE EXISTING MINOR OBJECTIVES
		for (SchemeObjectiveEntity objective : existingObjectives) {

			minorRepository.deleteByObjectiveId(objective.getObjectiveId());
		}

		// 3. DELETE EXISTING MAJOR OBJECTIVES
		if (!existingObjectives.isEmpty()) {
			objectiveRepository.deleteBySchemeId(schemeId);
		}

		// 4. SAVE NEW OBJECTIVES
		List<SchemeObjectivesResponse> responses = new ArrayList<>();

		int majorOrder = 1;

		for (MajorObjectiveRequest major : request.majorObjectives()) {

			if (major == null || major.majorObjective() == null || major.majorObjective().isBlank()) {

				throw new IllegalArgumentException("Major objective cannot be empty");
			}

			// SAVE MAJOR OBJECTIVE
			SchemeObjectiveEntity objective = new SchemeObjectiveEntity();

			objective.setSchemeId(schemeId);
			objective.setMajorObjective(major.majorObjective().trim());
			objective.setDisplayOrder(majorOrder++);

			SchemeObjectiveEntity savedObjective = objectiveRepository.save(objective);

			List<MinorObjectiveResponse> minorResponses = new ArrayList<>();

			// SAVE MINOR OBJECTIVES
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

			// ADD RESPONSE
			responses.add(new SchemeObjectivesResponse(savedObjective.getObjectiveId(),
					savedObjective.getMajorObjective(), savedObjective.getDisplayOrder(), minorResponses));
		}

		return responses;
	}

	// SAVE OR UPDATE STATE FUNDING DETAILS
	@Transactional
	public StateFundingResponse saveStateFunding(Long schemeId, StateFundingRequest request,
			MultipartFile stateAgreementFile, MultipartFile fundingDocumentFile) throws IOException {

		// 1. Mandatory Yes/No validation
		if (request.hasStateComponents() == null || request.hasOtherFundingAgency() == null) {
			throw new IllegalArgumentException("Please select Yes or No for both sections");
		}

		// 2. State onboarding validation
		if (Boolean.TRUE.equals(request.hasStateComponents()) && request.stateOnboard() == null) {
			throw new IllegalArgumentException("Please select whether the state is onboard");
		}

		// 3. Other funding source validation
		if (Boolean.TRUE.equals(request.hasOtherFundingAgency())
				&& (request.otherFundingSources() == null || request.otherFundingSources().isBlank())) {
			throw new IllegalArgumentException("Please specify other funding sources");
		}

		// 4. Find existing record or create new
		SchemeStateFundingEntity fundingEntity = fundingRepository.findBySchemeId(schemeId)
				.orElseGet(SchemeStateFundingEntity::new);

		fundingEntity.setSchemeId(schemeId);
		fundingEntity.setHasStateComponents(request.hasStateComponents());

		// 5. State components
		if (Boolean.TRUE.equals(request.hasStateComponents())) {

			fundingEntity.setStateOnboard(request.stateOnboard());

			if (stateAgreementFile != null && !stateAgreementFile.isEmpty()) {

				// DocumentUploadResponse upload = uploadDocument(schemeId, "STATE_AGREEMENT",
				// stateAgreementFile);

				fundingEntity.setStateAgreementFileName(stateAgreementFile.getOriginalFilename());
				// fundingEntity.setStateAgreementFilePath(upload.filePath());
				fundingEntity.setStateAgreementFile(stateAgreementFile.getBytes());
			}

		} else {

			fundingEntity.setStateOnboard(null);
			fundingEntity.setStateAgreementFileName(null);
		}

		// 6. Other funding agency
		fundingEntity.setHasOtherFundingAgency(request.hasOtherFundingAgency());

		if (Boolean.TRUE.equals(request.hasOtherFundingAgency())) {

			fundingEntity.setOtherFundingSources(request.otherFundingSources());

			if (fundingDocumentFile != null && !fundingDocumentFile.isEmpty()) {

				// DocumentUploadResponse upload = uploadDocument(schemeId, "FUNDING_DOCUMENT",
				// fundingDocumentFile);

				fundingEntity.setFundingDocumentFileName(fundingDocumentFile.getOriginalFilename());
				fundingEntity.setFundingDocumentFile(fundingDocumentFile.getBytes());
			}

		} else {

			fundingEntity.setOtherFundingSources(null);
			fundingEntity.setFundingDocumentFileName(null);

		}

		// 7. Save
		SchemeStateFundingEntity saved = fundingRepository.save(fundingEntity);

		return mapFundingResponse(saved);
	}

	// UPLOAD DOCUMENT
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

		if (!Set.of(".pdf", ".doc", ".docx", ".jpg", "jpeg", ".png").contains(extension)) {
			throw new IllegalArgumentException("Only PDF, DOC, DOCX,jpg ,jpeg and png files are allowed");
		}

		try {
			Path directory = Paths.get(UPLOAD_DIR + schemeId).toAbsolutePath().normalize();

			Files.createDirectories(directory);

			String storedName = originalName;

			Path target = directory.resolve(storedName).normalize();

			if (!target.startsWith(directory)) {
				throw new IllegalArgumentException("Invalid file path");
			}

			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

			return new DocumentUploadResponse(originalName, target.toString(), "File uploaded successfully");

		} catch (IOException e) {
			throw new RuntimeException("Document upload failed", e);
		}
	}

	private StateFundingResponse mapFundingResponse(SchemeStateFundingEntity entity) {

		return new StateFundingResponse(entity.getId(), entity.getSchemeId(), entity.getHasStateComponents(),
				entity.getStateOnboard(), entity.getStateAgreementFileName(), entity.getHasOtherFundingAgency(),
				entity.getOtherFundingSources(), entity.getFundingDocumentFileName(), entity.getStateAgreementFile(),
				entity.getFundingDocumentFile());
	}

	@Override
	@Transactional(readOnly = true)
	public SchemeFundingObjectivesResponse getFundingAndObjectives(Long schemeId) {

		// FETCH STATE FUNDING
		SchemeStateFundingEntity entity = fundingRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new RuntimeException("State funding details not found for scheme ID: " + schemeId));

		StateFundingResponse fundingResponse = mapFundingResponse(entity);

		// FETCH OBJECTIVES
		List<SchemeObjectiveEntity> objectives = objectiveRepository.findBySchemeIdOrderByDisplayOrderAsc(schemeId);

		List<SchemeObjectivesResponse> objectiveResponses = new ArrayList<>();

		for (SchemeObjectiveEntity objective : objectives) {

			List<MinorObjectiveResponse> minors = minorRepository
					.findByObjectiveIdOrderByDisplayOrderAsc(objective.getObjectiveId()).stream()
					.map(m -> new MinorObjectiveResponse(m.getMinorObjectiveId(), m.getMinorObjective(),
							m.getDisplayOrder()))
					.toList();

			objectiveResponses.add(new SchemeObjectivesResponse(objective.getObjectiveId(),
					objective.getMajorObjective(), objective.getDisplayOrder(), minors));
		}

		return new SchemeFundingObjectivesResponse(fundingResponse, objectiveResponses);
	}
}