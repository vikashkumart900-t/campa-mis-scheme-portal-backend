package com.campa.mis.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scheme_role_status", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeRoleStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false, unique = true)
	private Long schemeId;

	@Column(name = "hod_status", length = 30)
	private String hodStatus;

	@Column(name = "pd_status", length = 30)
	private String pdStatus;

	@Column(name = "campa_status", length = 30)
	private String campaStatus;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}