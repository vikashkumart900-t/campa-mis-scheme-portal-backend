package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.campa.mis.dto.request.EmploymentGenerationRequest;
import com.campa.mis.dto.request.SchemeSupportingRequest;
import com.campa.mis.dto.request.SupportingDocumentRequest;
import com.campa.mis.entity.SchemeCapacityBuildingEntity;
import com.campa.mis.entity.SchemeEmploymentGenerationEntity;
import com.campa.mis.entity.SchemeSupportingDocumentEntity;
import com.campa.mis.repo.SchemeCapacityBuildingRepository;
import com.campa.mis.repo.SchemeEmploymentGenerationRepository;
import com.campa.mis.repo.SchemeSupportingDocumentRepository;
import com.campa.mis.service.SchemeDocEmploymentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchemeDocEmploymentDtlsServiceImpl implements SchemeDocEmploymentService {

	private final SchemeCapacityBuildingRepository capacityRepository;
	private final SchemeSupportingDocumentRepository documentRepository;
	private final SchemeEmploymentGenerationRepository employmentRepository;

	@Override
	@Transactional
	public SchemeSupportingRequest saveDocEmploymentDetails(Long schemeId, SchemeSupportingRequest request,
			List<MultipartFile> kmlFiles, List<MultipartFile> pngFiles) throws IOException {

		// 1. SAVE / UPDATE CAPACITY BUILDING DETAILS

		if (request.capacityBuildingDetails() != null) {

			SchemeCapacityBuildingEntity capacity = this.capacityRepository.findFirstBySchemeIdOrderByIdDesc(schemeId)
					.orElseGet(SchemeCapacityBuildingEntity::new);

			capacity.setSchemeId(schemeId);
			capacity.setCapacityBuildingDetails(request.capacityBuildingDetails());

			this.capacityRepository.save(capacity);
		}

		// 2. SAVE MULTIPLE KML / KMZ FILES

		List<SupportingDocumentRequest> documents = request.documents() != null ? request.documents() : List.of();

		List<MultipartFile> uploadedKmlFiles = kmlFiles != null ? kmlFiles : List.of();

		List<MultipartFile> uploadedPngFiles = pngFiles != null ? pngFiles : List.of();

		// Validate metadata count
		if (documents.size() != uploadedKmlFiles.size() + uploadedPngFiles.size()) {
			throw new IllegalArgumentException("Document metadata count must match uploaded files count");
		}

		this.documentRepository.deleteBySchemeId(schemeId);

		// 1. SAVE / UPDATE KML FILES
		for (int i = 0; i < uploadedKmlFiles.size(); i++) {

			SupportingDocumentRequest doc = documents.get(i);
			MultipartFile file = uploadedKmlFiles.get(i);

			saveSupportingDocument(schemeId, doc, file);
		}

		// 2. SAVE / UPDATE PNG FILES
		for (int i = 0; i < uploadedPngFiles.size(); i++) {

			SupportingDocumentRequest doc = documents.get(uploadedKmlFiles.size() + i);

			MultipartFile file = uploadedPngFiles.get(i);

			saveSupportingDocument(schemeId, doc, file);
		}

		// 4. SAVE / UPDATE EMPLOYMENT GENERATION DETAILS

		if (request.employmentDetails() != null) {

			this.employmentRepository.deleteBySchemeId(schemeId);

			for (EmploymentGenerationRequest employment : request.employmentDetails()) {

				if (employment.financialYear() == null || employment.financialYear().isBlank()) {
					throw new IllegalArgumentException("Financial year is required");
				}

				if (employment.labourCost() == null || employment.labourCost().compareTo(BigDecimal.ZERO) < 0) {
					throw new IllegalArgumentException("Labour cost must be zero or greater");
				}

				if (employment.mandaysGenerated() == null
						|| employment.mandaysGenerated().compareTo(BigDecimal.ZERO) < 0) {
					throw new IllegalArgumentException("Mandays generated must be zero or greater");
				}

				if (employment.tribals() == null || employment.tribals() < 0) {
					throw new IllegalArgumentException("Tribals count must be zero or greater");
				}

				if (employment.forestDwellersBenefitted() == null || employment.forestDwellersBenefitted() < 0) {
					throw new IllegalArgumentException("Forest dwellers count must be zero or greater");
				}

				SchemeEmploymentGenerationEntity entity = new SchemeEmploymentGenerationEntity();

				entity.setSchemeId(schemeId);
				entity.setFinancialYear(employment.financialYear());
				entity.setLabourCost(employment.labourCost());
				entity.setMandaysGenerated(employment.mandaysGenerated());
				entity.setTribals(employment.tribals());
				entity.setForestDwellersBenefitted(employment.forestDwellersBenefitted());

				this.employmentRepository.save(entity);
			}
		}

		return request;
	}

	private void saveSupportingDocument(Long schemeId, SupportingDocumentRequest doc, MultipartFile file)
			throws IOException {

		if (doc.documentType() == null || doc.documentType().isBlank()) {
			throw new IllegalArgumentException("Document type is required");
		}

		if (doc.description() == null || doc.description().isBlank()) {
			throw new IllegalArgumentException("Document description is required");
		}

		String documentType = doc.documentType().trim().toUpperCase(Locale.ROOT);

		validateFile(file, documentType);

		// Always create a new entity
		SchemeSupportingDocumentEntity supportingDocumentEntity = new SchemeSupportingDocumentEntity();

		supportingDocumentEntity.setSchemeId(schemeId);
		supportingDocumentEntity.setDocumentType(documentType);
		supportingDocumentEntity.setDescription(doc.description());
		supportingDocumentEntity.setContentType(file.getContentType());
		supportingDocumentEntity.setFileName(file.getOriginalFilename());
		supportingDocumentEntity.setFileData(file.getBytes());

		documentRepository.save(supportingDocumentEntity);
	}

	// 5. FILE VALIDATION

	private void validateFile(MultipartFile file, String type) {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Please select a file");
		}

		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Document type is required");
		}

		String originalFilename = file.getOriginalFilename();

		if (originalFilename == null || originalFilename.isBlank()) {
			throw new IllegalArgumentException("Invalid file name");
		}
		if (file.getSize() >= 20L * 1024 * 1024) {
			throw new IllegalArgumentException("KML file size cannot exceed 20 MB");
		}

		type = type.trim().toUpperCase(Locale.ROOT);

		if (!"KML".equalsIgnoreCase(type) && !"KMZ".equalsIgnoreCase(type) && !"IMAGE".equalsIgnoreCase(type)) {

			throw new IllegalArgumentException("Invalid document type. Allowed types: KML, KMZ and IMAGE");
		}
	}
}
