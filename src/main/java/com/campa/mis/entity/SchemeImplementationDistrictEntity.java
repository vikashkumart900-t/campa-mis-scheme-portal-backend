
package com.campa.mis.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_implementation_districts", schema = "mis_scheme", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "scheme_implementation_state_id", "district_code" }) })
@Getter
@Setter
public class SchemeImplementationDistrictEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_implementation_state_id", nullable = false)
	private Long schemeImplementationStateId;

	@Column(name = "district_code", nullable = false)
	private String districtCode;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
