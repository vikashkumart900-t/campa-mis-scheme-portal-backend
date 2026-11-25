
package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.EvaluationResearchRequest;
import com.campa.mis.dto.response.EvaluationResearchResponse;
import com.campa.mis.entity.SchemeEvaluationResearchEntity;
import com.campa.mis.repo.SchemeEvaluationResearchRepository;
import com.campa.mis.service.SchemeEvaluationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeEvaluationServiceImpl implements SchemeEvaluationService {

	private final SchemeEvaluationResearchRepository evaluationRepository;

	// SAVE / UPDATE
	@Override
	@Transactional
	public EvaluationResearchResponse saveEvaluationResearch(Long schemeId, EvaluationResearchRequest request,
			MultipartFile file) throws IOException {

		// 1. Validate request
		if (request.evaluationType() == null) {
			throw new IllegalArgumentException("Please select Annual or Midterm Evaluation");
		}

		if (request.evaluationDetails() == null || request.evaluationDetails().isEmpty()) {
			throw new IllegalArgumentException("Please enter evaluation details");
		}

		if (request.researchAspects() == null || request.researchAspects().isBlank()) {
			throw new IllegalArgumentException("Research aspects are required");
		}

		// 2. Find existing record or create new
		SchemeEvaluationResearchEntity evaluationResearchEntity = evaluationRepository
				.findFirstBySchemeIdOrderByIdDesc(schemeId).orElseGet(SchemeEvaluationResearchEntity::new);

		boolean isNewRecord = evaluationResearchEntity.getId() == null;

		// 3. Validate evaluation details
		for (String detail : request.evaluationDetails()) {

			if (detail == null || detail.isBlank()) {
				throw new IllegalArgumentException("Evaluation details cannot be blank");
			}

			if (detail.trim().split("\\s+").length > 500) {
				throw new IllegalArgumentException("Each evaluation detail must not exceed 500 words");
			}
		}

		// 4. Set basic fields
		evaluationResearchEntity.setSchemeId(schemeId);
		evaluationResearchEntity.setEvaluationType(request.evaluationType());
		evaluationResearchEntity.setEvaluationDetails(request.evaluationDetails());
		evaluationResearchEntity.setResearchAspects(request.researchAspects());
		evaluationResearchEntity.setUniqueHighlights(request.uniqueHighlights());

		// 5. Process supporting document
		if (file != null && !file.isEmpty()) {

			validateFile(file);

			String originalName = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName()
					.toString();

			evaluationResearchEntity.setDocumentFileName(originalName);

			evaluationResearchEntity.setDocumentContentType(
					file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);

			evaluationResearchEntity.setSupportingDocument(file.getBytes());

		} else if (isNewRecord) {
			throw new IllegalArgumentException("Supporting document is required");
		}

		// 6. Save database record
		SchemeEvaluationResearchEntity saved = evaluationRepository.save(evaluationResearchEntity);

		// 7. Return response
		return mapResponse(saved);
	}

	// FILE VALIDATION
	private void validateFile(MultipartFile file) {

		if (file.isEmpty()) {
			throw new IllegalArgumentException("Please select a supporting document");
		}

		// Maximum file size: 20 MB
		long maxFileSize = 20L * 1024 * 1024;

		if (file.getSize() > maxFileSize) {
			throw new IllegalArgumentException("Maximum file size allowed is 20 MB");
		}

		String originalName = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

		String extension = originalName.contains(".")
				? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
				: "";

		if (!Set.of(".pdf", ".doc", ".docx").contains(extension)) {
			throw new IllegalArgumentException("Only PDF, DOC and DOCX files are allowed");
		}
	}

	// FETCH EVALUATION DETAILS
	@Override
	@Transactional(readOnly = true)
	public EvaluationResearchResponse getEvaluationResearch(Long schemeId) {

		SchemeEvaluationResearchEntity entity = evaluationRepository.findFirstBySchemeIdOrderByIdDesc(schemeId)
				.orElseThrow(() -> new IllegalArgumentException("Evaluation details not found"));

		return mapResponse(entity);
	}

	// RESPONSE MAPPER
	private EvaluationResearchResponse mapResponse(SchemeEvaluationResearchEntity entity) {

		return new EvaluationResearchResponse(entity.getId(), entity.getSchemeId(), entity.getEvaluationType(),
				entity.getEvaluationDetails(), entity.getDocumentFileName(), entity.getDocumentContentType(),
				entity.getResearchAspects(), entity.getUniqueHighlights(), entity.getSupportingDocument());
	}
}
