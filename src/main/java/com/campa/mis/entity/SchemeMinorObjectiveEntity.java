package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheme_minor_objectives", schema = "mis_scheme")
@Getter
@Setter
public class SchemeMinorObjectiveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "minor_objective_id")
	private Long minorObjectiveId;

	@Column(name = "objective_id", nullable = false)
	private Long objectiveId;

	@Column(name = "minor_objective", nullable = false, length = 1000)
	private String minorObjective;

	@Column(name = "display_order")
	private Integer displayOrder;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}
}