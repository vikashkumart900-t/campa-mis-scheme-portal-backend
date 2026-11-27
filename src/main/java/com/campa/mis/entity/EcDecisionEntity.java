package com.campa.mis.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ec_decisions", schema = "mis_scheme")
@Getter
@Setter
public class EcDecisionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ec_decision_id")
	private Long ecDecisionId;

	@Column(name = "scheme_id", nullable = false, unique = true)
	private Long schemeId;

	@Column(name = "meeting_date", nullable = false)
	private LocalDate meetingDate;

	@Column(name = "agenda_item_no", nullable = false)
	private String agendaItemNo;

	@Column(name = "agenda_subject", nullable = false)
	private String agendaSubject;

	@Column(name = "ec_decision", nullable = false)
	private String ecDecision;

	@Column(name = "ec_remarks", nullable = false)
	private String ecRemarks;

	@Column(name = "meeting_minutes_file_name")
	private String meetingMinutesFileName;

	@Column(name = "meeting_minutes_content_type")
	private String meetingMinutesContentType;

	@Column(name = "meeting_minutes_file_data", columnDefinition = "bytea")
	private byte[] meetingMinutesFileData;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = OffsetDateTime.now();
	}
}