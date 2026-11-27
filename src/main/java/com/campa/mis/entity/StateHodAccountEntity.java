
package com.campa.mis.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state_hod_accounts", schema = "mis_scheme")
@Getter
@Setter
public class StateHodAccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "state_code", nullable = false)
	private String stateCode;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "designation")
	private String designation;

	@Column(name = "scheme_id")
	private Long schemeId;

	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "status")
	private boolean status;

	@PrePersist
	public void onCreate() {
		createdAt = OffsetDateTime.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = OffsetDateTime.now();
	}
}
