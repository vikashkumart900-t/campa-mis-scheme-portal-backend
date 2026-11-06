package com.campa.mis.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import com.campa.mis.dto.request.LoginRequestDTO;
import com.campa.mis.dto.request.NodalOfficerRegistrationRequestDTO;
import com.campa.mis.dto.request.RegistrationRequestDTO;
import com.campa.mis.dto.request.ResetPasswordRequest;
import com.campa.mis.dto.response.LoginResponse;
import com.campa.mis.dto.response.ProgramOrganizationsResponseDTO;
import com.campa.mis.dto.response.SchemeDetailsResponse;
import com.campa.mis.entity.DistrictEntity;
import com.campa.mis.entity.SchemeEntity;
import com.campa.mis.entity.StateEntity;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;

public interface SchemeService {

	Map<String, Object> getDropdownData(Long organizationId);

	List<DistrictEntity> getDistrictsByStateCode(String stateCode);

	List<StateEntity> getAllStates();

	String saveNodalOfficerData(NodalOfficerRegistrationRequestDTO request);

	List<SchemeEntity> getAllActiveSchemes();

	ResponseEntity<String> pariveshLogin(LoginRequestDTO request);

	Optional<SchemeDetailsResponse> getSchemeDetailsById(Long schemeId);

	List<UserRoleEntity> getAllRoles();

	List<ProgramOrganizationsResponseDTO> getAllDivisionOrganizations();

	UserAccountEntity register(RegistrationRequestDTO request);

	LoginResponse misLogin(LoginRequestDTO request);

	void logout(String email, String tokenId);

	void forgotPassword(String email);

	String verifyOtp(String email, String otp);

	void resetPassword(ResetPasswordRequest request);

}