package com.campa.mis.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "program_divisions", schema = "mis_scheme")
@Data
public class ProgramDivisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_division_id")
    private Long programDivisionId;

    @Column(name = "division_name", nullable = false, length = 250)
    private String divisionName;

    @Column(name = "division_code", length = 50)
    private String divisionCode;

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