package com.campa.mis.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "gb_state_decisions",
    schema = "mis_scheme",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_gb_decision_state",
            columnNames = {"gb_decision_id", "state_code"}
        )
    }
)
@Getter
@Setter
public class GbStateDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gb_state_decision_id")
    private Long gbStateDecisionId;

    @Column(name = "gb_decision_id", nullable = false)
    private Long gbDecisionId;

    @Column(name = "state_code", nullable = false)
    private String stateCode;

    @Column(name = "is_selected_for_state_apo")
    private Boolean isSelectedForStateApo;

    @Column(name = "state_gb_decision")
    private String stateGbDecision;

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