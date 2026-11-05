package com.campa.mis.dto.request;

public record NodalOfficerRegistrationRequestDTO(String officerName, String designation, String email, String mobile,
		String password, Long organizationId) {

}
