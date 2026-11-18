
package com.campa.mis.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheme_implementation_states", schema = "mis_scheme", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "scheme_id", "state_code" }) })
@Getter
@Setter
public class SchemeImplementationStateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheme_id", nullable = false)
	private Long schemeId;

	@Column(name = "state_code", nullable = false)
	private String stateCode;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
