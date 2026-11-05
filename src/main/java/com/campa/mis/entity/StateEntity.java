package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "states", schema = "mis_scheme")
@Data
public class StateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name", nullable = false, length = 100)
    private String stateName;

    @Column(name = "state_code", length = 10)
    private String stateCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }
}