package com.campa.mis.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "password_reset_requests", schema = "mis_scheme")
public class PasswordResetRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reset_id")
	private Long resetId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "otp_hash")
	private String otpHash;

	@Column(name = "reset_token_hash")
	private String resetTokenHash;

	@Column(name = "expires_at")
	private OffsetDateTime expiresAt;

	@Column(name = "reset_token_expires_at")
	private OffsetDateTime resetTokenExpiresAt;

	@Column(name = "attempts")
	private Integer attempts = 0;

	@Column(name = "verified_at")
	private OffsetDateTime verifiedAt;

	@Column(name = "consumed_at")
	private OffsetDateTime consumedAt;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}

}