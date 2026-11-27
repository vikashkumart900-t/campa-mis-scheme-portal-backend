package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import com.campa.mis.dto.request.EcDecisionRequest;
import com.campa.mis.dto.request.EcGbDecisionRequest;
import com.campa.mis.dto.request.GbDecisionRequest;
import com.campa.mis.dto.request.GbStateDecisionRequest;
import com.campa.mis.dto.response.EcDecisionResponse;
import com.campa.mis.dto.response.EcGbDecisionResponse;
import com.campa.mis.dto.response.GbDecisionResponse;
import com.campa.mis.dto.response.GbStateDecisionResponse;
import com.campa.mis.entity.EcDecisionEntity;
import com.campa.mis.entity.GbDecisionEntity;
import com.campa.mis.entity.GbStateDecisionEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.SchemeRoleStatusEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.repo.EcDecisionRepository;
import com.campa.mis.repo.GbDecisionRepository;
import com.campa.mis.repo.GbStateDecisionRepository;
import com.campa.mis.repo.SchemeRepository;
import com.campa.mis.repo.SchemeRoleStatusRepository;
import com.campa.mis.repo.StateRepository;
import com.campa.mis.service.EcGbDecisionService;

@Service
@RequiredArgsConstructor
public class EcGbDecisionServiceImpl implements EcGbDecisionService {

	private final EcDecisionRepository ecRepository;
	private final GbDecisionRepository gbRepository;
	private final GbStateDecisionRepository gbStateRepository;
	private final StateRepository stateRepository;
	private final SchemeRoleStatusRepository schemeRoleStatusRepository;
	private final SchemeRepository schemeRepository;

	@Override
	@Transactional
	public EcGbDecisionResponse saveEcGbDecision(Long schemeId, EcGbDecisionRequest request,
			MultipartFile ecMinutesFile, MultipartFile gbMinutesFile) {

		// STEP 1: Save EC decision
		EcDecisionResponse ecResponse = saveEcDecision(schemeId, request.ecDecision(), ecMinutesFile);

		// STEP 2: Check EC decision
		if ("RETURNED".equalsIgnoreCase(request.ecDecision().ecDecision())) {

			return new EcGbDecisionResponse(
					"EC decision saved successfully. Proposal returned by EC. GB data was not saved.", ecResponse, null,
					false);
		}

		// STEP 3: EC APPROVED - GB request is mandatory
		if (request.gbDecision() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"GB decision details are required when EC decision is APPROVED");
		}

		// STEP 4: Save GB decision and state-wise decisions
		GbDecisionResponse gbResponse = saveGbDecision(schemeId, request.gbDecision(), gbMinutesFile);

		return new EcGbDecisionResponse("EC and GB decisions saved successfully.", ecResponse, gbResponse, true);
	}

	// ---------------- EC SAVE / UPDATE ----------------

	@Transactional
	public EcDecisionResponse saveEcDecision(Long schemeId, EcDecisionRequest request, MultipartFile minutesFile) {

		EcDecisionEntity entity = ecRepository.findBySchemeId(schemeId).orElseGet(EcDecisionEntity::new);

		entity.setSchemeId(schemeId);
		entity.setMeetingDate(request.meetingDate());
		entity.setAgendaItemNo(request.agendaItemNo());
		entity.setAgendaSubject(request.agendaSubject());
		entity.setEcDecision(request.ecDecision());
		entity.setEcRemarks(request.ecRemarks());
		// entity.setDecisionStatus(request.decisionStatus());

		if (minutesFile != null && !minutesFile.isEmpty()) {
			validatePdf(minutesFile);

			try {
				entity.setMeetingMinutesFileName(minutesFile.getOriginalFilename());
				entity.setMeetingMinutesContentType(minutesFile.getContentType());
				entity.setMeetingMinutesFileData(minutesFile.getBytes());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to read EC meeting minutes file");
			}
		}

		EcDecisionEntity saved = ecRepository.save(entity);

		return mapEcResponse(saved);
	}

	// ---------------- EC FETCH ----------------

	@Override
	@Transactional(readOnly = true)
	public EcDecisionResponse getEcDecision(Long schemeId) {

		EcDecisionEntity entity = ecRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"EC decision not found for scheme ID: " + schemeId));

		return mapEcResponse(entity);
	}

	// ---------------- EC FILE DOWNLOAD ----------------

	@Override
	@Transactional(readOnly = true)
	public byte[] downloadEcMinutes(Long schemeId) {

		EcDecisionEntity entity = ecRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EC decision not found"));

		if (entity.getMeetingMinutesFileData() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EC meeting minutes file not found");
		}

		return entity.getMeetingMinutesFileData();
	}

	// ---------------- GB SAVE / UPDATE ----------------

	@Transactional
	public GbDecisionResponse saveGbDecision(Long schemeId, GbDecisionRequest request, MultipartFile minutesFile) {

		// EC must be approved before GB decision entry
		EcDecisionEntity ec = ecRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"EC decision must be entered before GB decision"));

		if (!"APPROVED".equalsIgnoreCase(ec.getEcDecision())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"GB decision cannot be entered because EC decision is not approved");
		}

		GbDecisionEntity entity = gbRepository.findBySchemeId(schemeId).orElseGet(GbDecisionEntity::new);

		entity.setSchemeId(schemeId);
		entity.setMeetingDate(request.meetingDate());
		entity.setAgendaItemNo(request.agendaItemNo());
		entity.setAgendaSubject(request.agendaSubject());
		entity.setGbDecision(request.gbDecision());
		entity.setGbRemarks(request.gbRemarks());
		// entity.setDecisionStatus(request.decisionStatus());

		if (minutesFile != null && !minutesFile.isEmpty()) {
			validatePdf(minutesFile);

			try {
				entity.setMeetingMinutesFileName(minutesFile.getOriginalFilename());
				entity.setMeetingMinutesContentType(minutesFile.getContentType());
				entity.setMeetingMinutesFileData(minutesFile.getBytes());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to read GB meeting minutes file");
			}
		}

		GbDecisionEntity saved = gbRepository.save(entity);

		// Replace existing state decisions with submitted list
		gbStateRepository.deleteByGbDecisionId(saved.getGbDecisionId());

		List<GbStateDecisionEntity> stateEntities = request.stateDecisions().stream()
				.map(stateRequest -> mapStateEntity(saved.getGbDecisionId(), stateRequest)).toList();

		gbStateRepository.saveAll(stateEntities);

		return mapGbResponse(saved, stateEntities, null,null,null);
	}

	// ---------------- GB FETCH ----------------

	@Override
	@Transactional(readOnly = true)
	public GbDecisionResponse getGbDecision(Long schemeId) {

		SchemeRoleStatusEntity schemeRoleStatusEntity = schemeRoleStatusRepository.findBySchemeId(schemeId)
				.orElse(null);

		LocalDateTime campaUpdatedAt = schemeRoleStatusEntity != null ? schemeRoleStatusEntity.getCampaUpdatedAt()
				: null;

		Optional<SchemeEntity> schemeEntity = schemeRepository.findById(schemeId);

		String schemeName = schemeEntity.map(SchemeEntity::getSchemeName).orElse(null);

		String proposalId = "PROP/" + LocalDate.now().getYear() + "/" + schemeId;

		GbDecisionEntity entity = gbRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"GB decision not found for scheme ID: " + schemeId));

		List<GbStateDecisionEntity> states = gbStateRepository.findByGbDecisionId(entity.getGbDecisionId());

		return mapGbResponse(entity, states, campaUpdatedAt,schemeName,proposalId);
	}

	// ---------------- GB FILE DOWNLOAD ----------------

	@Override
	@Transactional(readOnly = true)
	public byte[] downloadGbMinutes(Long schemeId) {

		GbDecisionEntity entity = gbRepository.findBySchemeId(schemeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GB decision not found"));

		if (entity.getMeetingMinutesFileData() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GB meeting minutes file not found");
		}

		return entity.getMeetingMinutesFileData();
	}

	// ---------------- MAPPERS ----------------

	private EcDecisionResponse mapEcResponse(EcDecisionEntity entity) {

		return new EcDecisionResponse(entity.getEcDecisionId(), entity.getSchemeId(), entity.getMeetingDate(),
				entity.getAgendaItemNo(), entity.getAgendaSubject(), entity.getEcDecision(), entity.getEcRemarks(),
				entity.getMeetingMinutesFileName(), entity.getMeetingMinutesContentType());
	}

	private GbStateDecisionEntity mapStateEntity(Long gbDecisionId, GbStateDecisionRequest request) {

		GbStateDecisionEntity gbStateDecisionEntity = new GbStateDecisionEntity();

		gbStateDecisionEntity.setGbDecisionId(gbDecisionId);
		gbStateDecisionEntity.setStateCode(request.stateCode());

		gbStateDecisionEntity.setIsSelectedForStateApo(request.isSelectedForStateApo());

		gbStateDecisionEntity
				.setStateGbDecision(Boolean.TRUE.equals(request.isSelectedForStateApo()) ? "APPROVED" : "NOT_APPROVED");

		return gbStateDecisionEntity;
	}

	private GbDecisionResponse mapGbResponse(GbDecisionEntity entity, List<GbStateDecisionEntity> states,
			LocalDateTime campaApprovalDate,String schemeName,
		    String proposalID) {

		List<GbStateDecisionResponse> stateResponses = states.stream().map(state -> {

			String stateName = stateRepository.findByStateCode(state.getStateCode()).map(StateEntity::getStateName)
					.orElse(null);

			return new GbStateDecisionResponse(state.getGbStateDecisionId(), state.getStateCode(), stateName,
					state.getIsSelectedForStateApo(), state.getStateGbDecision());
		}).toList();

		return new GbDecisionResponse(entity.getGbDecisionId(), entity.getSchemeId(), entity.getMeetingDate(),
				entity.getAgendaItemNo(), entity.getAgendaSubject(), entity.getGbDecision(), entity.getGbRemarks(),
				entity.getMeetingMinutesFileName(), entity.getMeetingMinutesContentType(), stateResponses,
				campaApprovalDate,schemeName,proposalID);
	}

	private void validatePdf(MultipartFile file) {

		String contentType = file.getContentType();

		if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
		}

		if (file.getSize() > 10 * 1024 * 1024 * 1024 * 1024) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must not exceed 30 MB");
		}
	}
}