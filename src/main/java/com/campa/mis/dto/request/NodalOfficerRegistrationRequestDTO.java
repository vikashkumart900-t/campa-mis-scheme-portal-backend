package com.campa.mis.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record NodalOfficerRegistrationRequestDTO(

		@NotBlank String officerName,

		@NotBlank String designation,

		@NotBlank @Email String email,

		@NotBlank @Pattern(regexp = "^[0-9]{10}$") String mobile,

		String aadhaarNumber,

		String schemeDuration,

		BigDecimal proposedFinancialOutlay,

		LocalDate dateOfProposal,

		LocalDate expectedCompletionDate,

		@NotNull Long organizationId,

		Long divisionId,

		@NotNull Long schemeId,

		@NotEmpty List<@Valid StateDistrictRequestDTO> stateDistricts

) {

	public record StateDistrictRequestDTO(

			@NotBlank String stateCode,

			@NotEmpty List<@NotBlank String> districtCodes

	) {}
}