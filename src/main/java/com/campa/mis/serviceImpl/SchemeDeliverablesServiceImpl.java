package com.campa.mis.serviceImpl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campa.mis.dto.request.DeliverableRequest;
import com.campa.mis.dto.request.DeliverablesRequest;
import com.campa.mis.dto.request.YearlyExpenditureRequest;
import com.campa.mis.dto.response.DeliverablesResponse;
import com.campa.mis.entity.SchemeDeliverableDetailsEntity;
import com.campa.mis.entity.SchemeDeliverablesEntity;
import com.campa.mis.entity.SchemeYearlyExpenditureEntity;
import com.campa.mis.repo.SchemeDeliverableDetailsRepository;
import com.campa.mis.repo.SchemeDeliverablesRepository;
import com.campa.mis.repo.SchemeYearlyExpenditureRepository;
import com.campa.mis.service.SchemeDeliverablesService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeDeliverablesServiceImpl implements SchemeDeliverablesService {

	private final SchemeDeliverableDetailsRepository detailsRepository;

	private final SchemeDeliverablesRepository deliverablesRepository;

	private final SchemeYearlyExpenditureRepository expenditureRepository;

	@Override
	@Transactional
	public DeliverablesResponse saveDeliverables(Long schemeId, DeliverablesRequest request) {

		SchemeDeliverableDetailsEntity existing = detailsRepository.findBySchemeId(schemeId).orElse(null);

		return saveOrUpdate(schemeId, request, existing);
	}

	@Override
	@Transactional
	public DeliverablesResponse updateDeliverables(Long schemeId, DeliverablesRequest request) {

		SchemeDeliverableDetailsEntity existing = detailsRepository.findBySchemeId(schemeId).orElseThrow(
				() -> new IllegalArgumentException("Deliverable details not found for scheme: " + schemeId));

		return saveOrUpdate(schemeId, request, existing);
	}

	private DeliverablesResponse saveOrUpdate(Long schemeId, DeliverablesRequest request,
			SchemeDeliverableDetailsEntity existing) {

		validateRequest(request);

		// 1. Save parent details

		SchemeDeliverableDetailsEntity details = existing != null ? existing : new SchemeDeliverableDetailsEntity();

		details.setSchemeId(schemeId);
		details.setExpenditureType(request.expenditureType());
		details.setNationalImportance(request.nationalImportance());
		details.setInternationalFootprints(request.internationalFootprints());

		details = detailsRepository.save(details);

		Long detailsId = details.getId();

		// 2. Delete existing child records during update

		if (existing != null) {
			deliverablesRepository.deleteByDetailsId(detailsId);
			expenditureRepository.deleteByDetailsId(detailsId);
		}

		// 3. Save deliverables

		List<SchemeDeliverablesEntity> deliverables = request.deliverables().stream().map(item -> {

			SchemeDeliverablesEntity schemeDeliverablesEntity = new SchemeDeliverablesEntity();

			schemeDeliverablesEntity.setDeliverableDetailsId(detailsId);
			schemeDeliverablesEntity.setDeliverable(item.deliverable());
			schemeDeliverablesEntity.setUnit(item.unit());
			schemeDeliverablesEntity.setInitiationDate(item.initiationDate());
			schemeDeliverablesEntity.setClosingDate(item.closingDate());
			schemeDeliverablesEntity.setFinancialOutlay(item.financialOutlay());
			schemeDeliverablesEntity.setRemarks(item.remarks());

			return schemeDeliverablesEntity;

		}).toList();

		deliverablesRepository.saveAll(deliverables);

		// 4. Save yearly expenditure only for YEAR_WISE

		if ("YEAR_WISE".equals(request.expenditureType())) {

			List<SchemeYearlyExpenditureEntity> expenditures = request.yearlyExpenditures().stream().map(item -> {

				SchemeYearlyExpenditureEntity yearlyExpenditureEntity = new SchemeYearlyExpenditureEntity();

				yearlyExpenditureEntity.setDeliverableDetailsId(detailsId);
				yearlyExpenditureEntity.setFinancialYear(item.financialYear());
				yearlyExpenditureEntity.setExpenditure(item.expenditure());

				return yearlyExpenditureEntity;

			}).toList();

			expenditureRepository.saveAll(expenditures);
		}

		// 5. Prepare response

		return buildResponse(details);
	}

	@Override
	@Transactional(readOnly = true)
	public DeliverablesResponse getDeliverables(Long schemeId) {

		SchemeDeliverableDetailsEntity details = detailsRepository.findBySchemeId(schemeId).orElseThrow(
				() -> new IllegalArgumentException("Deliverable details not found for scheme: " + schemeId));

		return buildResponse(details);
	}

	private void validateRequest(DeliverablesRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty");
		}

		if (!List.of("ACTIVITY_WISE", "YEAR_WISE").contains(request.expenditureType())) {
			throw new IllegalArgumentException("Invalid expenditure type");
		}

		if (request.deliverables() == null || request.deliverables().isEmpty()) {
			throw new IllegalArgumentException("At least one deliverable is required");
		}

		for (DeliverableRequest item : request.deliverables()) {

			if (item.deliverable() == null || item.deliverable().isBlank()) {
				throw new IllegalArgumentException("Deliverable name is required");
			}

			if (item.unit() == null || item.unit().isBlank()) {
				throw new IllegalArgumentException("Deliverable unit is required");
			}

			if (item.initiationDate() == null || item.closingDate() == null
					|| item.closingDate().isBefore(item.initiationDate())) {
				throw new IllegalArgumentException("Invalid deliverable dates");
			}

			if (item.financialOutlay() == null || item.financialOutlay().compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("Invalid financial outlay");
			}
		}

		if ("ACTIVITY_WISE".equals(request.expenditureType())) {

			if (request.yearlyExpenditures() != null && !request.yearlyExpenditures().isEmpty()) {
				throw new IllegalArgumentException("Yearly expenditures must be empty for ACTIVITY_WISE");
			}
		}

		if ("YEAR_WISE".equals(request.expenditureType())) {

			if (request.yearlyExpenditures() == null || request.yearlyExpenditures().isEmpty()) {
				throw new IllegalArgumentException("Yearly expenditures are required for YEAR_WISE");
			}

			for (YearlyExpenditureRequest item : request.yearlyExpenditures()) {

				if (item.financialYear() == null || item.financialYear().isBlank() || item.expenditure() == null
						|| item.expenditure().compareTo(BigDecimal.ZERO) < 0) {

					throw new IllegalArgumentException("Invalid yearly expenditure details");
				}
			}
		}
	}

	private DeliverablesResponse buildResponse(SchemeDeliverableDetailsEntity details) {

		List<SchemeDeliverablesEntity> deliverables = deliverablesRepository
				.findByDeliverableDetailsId(details.getId());

		List<SchemeYearlyExpenditureEntity> expenditures = expenditureRepository
				.findByDeliverableDetailsId(details.getId());

		BigDecimal totalOutlay = deliverables.stream().map(SchemeDeliverablesEntity::getFinancialOutlay)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		List<DeliverablesResponse.DeliverableResponse> deliverableResponses = deliverables.stream()
				.map(item -> new DeliverablesResponse.DeliverableResponse(item.getId(), item.getDeliverable(),
						item.getUnit(), item.getInitiationDate(), item.getClosingDate(), item.getFinancialOutlay(),
						item.getRemarks(), item.getDocumentFileName(), item.getDocumentFilePath()))
				.toList();

		List<DeliverablesResponse.YearlyExpenditureResponse> expenditureResponses = expenditures.stream()
				.map(item -> new DeliverablesResponse.YearlyExpenditureResponse(item.getId(), item.getFinancialYear(),
						item.getExpenditure()))
				.toList();

		return new DeliverablesResponse(details.getId(), details.getExpenditureType(), totalOutlay, deliverables.size(),
				deliverableResponses, expenditureResponses, details.getNationalImportance(),
				details.getInternationalFootprints());
	}
}