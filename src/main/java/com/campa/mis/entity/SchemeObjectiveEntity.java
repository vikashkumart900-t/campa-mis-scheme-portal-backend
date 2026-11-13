package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheme_objectives", schema = "mis_scheme")
@Getter
@Setter
public class SchemeObjectiveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "objective_id")
	private Long objectiveId;

	@Column(name = "scheme_id", nullable = false)
	private Long schemeId;

	@Column(name = "major_objective", nullable = false, length = 1000)
	private String majorObjective;

	@Column(name = "display_order")
	private int displayOrder;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}