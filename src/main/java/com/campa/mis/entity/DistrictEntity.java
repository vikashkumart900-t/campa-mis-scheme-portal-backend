package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "districts", schema = "mis_scheme")
@Data
public class DistrictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "state_code", nullable = false)
    private String stateCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "district_code", length = 20)
    private String districtCode;

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