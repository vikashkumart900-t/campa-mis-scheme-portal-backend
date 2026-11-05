package com.campa.mis.dto.response;

import java.time.OffsetDateTime;

public interface UserAccountProjection {

	Long getUserId();

	String getEmail();

	String getPasswordHash();

	String getAccountStatus();

	String getRoleCode();

	String getActiveTokenId();

	OffsetDateTime getSessionExpiresAt();
}