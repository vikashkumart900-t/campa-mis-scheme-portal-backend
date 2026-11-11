package com.campa.mis.exception;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		ResponseEntity<Map<String, Object>> errorResponse = GlobalExceptionHandler
				.unauthorizedResponse("Authentication required", request.getRequestURI());

		response.setStatus(errorResponse.getStatusCode().value());
		response.setContentType("application/json");

		objectMapper.writeValue(response.getOutputStream(), errorResponse.getBody());
	}
}