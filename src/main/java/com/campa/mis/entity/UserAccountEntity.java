
package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_accounts", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "organization_id")
	private Long organizationId;

	@Column(name = "program_division_id")
	private Long programDivisionId;

	@Column(name = "role_id", nullable = false)
	private Long roleId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "account_status")
	private String accountStatus = "PENDING_APPROVAL";

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "approved_at")
	private OffsetDateTime approvedAt;

	@Column(name = "rejection_reason", columnDefinition = "text")
	private String rejectionReason;

	@Column(name = "last_login_at")
	private OffsetDateTime lastLoginAt;

	@Column(name = "password_changed_at")
	private OffsetDateTime passwordChangedAt;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "designation")
	private String designation;

	@Column(name = "active_token_id")
	private String activeTokenId;

	@Column(name = "session_expires_at")
	private OffsetDateTime sessionExpiresAt;

	@PrePersist
	protected void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();

		if (createdAt == null) {
			createdAt = now;
		}

		if (updatedAt == null) {
			updatedAt = now;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = OffsetDateTime.now();
	}
}
