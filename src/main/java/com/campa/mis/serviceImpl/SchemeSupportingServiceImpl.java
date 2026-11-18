package com.campa.mis.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
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
public class SchemeSupportingServiceImpl implements SchemeDocEmploymentService {

	private final SchemeCapacityBuildingRepository capacityRepository;
	private final SchemeSupportingDocumentRepository documentRepository;
	private final SchemeEmploymentGenerationRepository employmentRepository;

	@Override
	@Transactional
	public String saveDocEmploymentDetails(Long schemeId, SchemeSupportingRequest request, List<MultipartFile> files)
			throws IOException {

		// 1. SAVE / UPDATE CAPACITY BUILDING DETAILS

		if (request.capacityBuildingDetails() != null) {

			SchemeCapacityBuildingEntity capacity = capacityRepository.findFirstBySchemeIdOrderByIdDesc(schemeId)
					.orElseGet(SchemeCapacityBuildingEntity::new);

			capacity.setSchemeId(schemeId);
			capacity.setCapacityBuildingDetails(request.capacityBuildingDetails());

			capacityRepository.save(capacity);
		}

		// 2. SAVE SUPPORTING DOCUMENTS

		List<SupportingDocumentRequest> documents = request.documents() != null ? request.documents() : List.of();

		List<MultipartFile> uploadedFiles = files != null ? files : List.of();

		if (documents.size() != uploadedFiles.size()) {
			throw new IllegalArgumentException("Number of uploaded files must match the number of documents");
		}

		for (int i = 0; i < documents.size(); i++) {

			SupportingDocumentRequest doc = documents.get(i);

			if (doc.description() == null || doc.description().isBlank()) {
				throw new IllegalArgumentException("Document description is required");
			}

			MultipartFile file = uploadedFiles.get(i);

			validateFile(file, doc.documentType());

			SchemeSupportingDocumentEntity document = new SchemeSupportingDocumentEntity();

			document.setSchemeId(schemeId);
			document.setDocumentType(doc.documentType().toUpperCase(Locale.ROOT));
			document.setDescription(doc.description());
			document.setContentType(file.getContentType());
			document.setFileData(file.getBytes());
			document.setFileSize(file.getSize());

			documentRepository.save(document);
		}

		// 3. SAVE EMPLOYMENT GENERATION DETAILS

		if (request.employmentDetails() != null) {

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

				employmentRepository.save(entity);
			}
		}

		return "Supporting documents and employment details saved successfully";
	}

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

		String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);

		type = type.trim().toUpperCase(Locale.ROOT);

		// KML / KMZ validation
		if ("KML".equals(type) || "KMZ".equals(type)) {

			if (!Set.of(".kml", ".kmz").contains(extension)) {
				throw new IllegalArgumentException("Only KML and KMZ files are allowed");
			}

			if (file.getSize() > 20L * 1024 * 1024) {
				throw new IllegalArgumentException("KML/KMZ file size cannot exceed 20 MB");
			}

		}
		// IMAGE validation
		else if ("IMAGE".equals(type)) {

			if (!Set.of(".jpg", ".jpeg", ".png").contains(extension)) {
				throw new IllegalArgumentException("Only JPG, JPEG and PNG images are allowed");
			}

			if (file.getSize() > 2L * 1024 * 1024) {
				throw new IllegalArgumentException("Image size cannot exceed 2 MB");
			}

		} else {
			throw new IllegalArgumentException("Invalid document type. Allowed types: KML, KMZ and IMAGE");
		}
	}
}
