package com.campa.mis.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

	private String message;
	private String accessToken;
	private String tokenType;
	private String expiresAt;
	private String email;
	private String role;

}
