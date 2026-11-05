package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sub_agencies", schema = "mis_scheme")
@Data
public class SubAgencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_agency_id")
    private Long subAgencyId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "sub_agency_name", nullable = false, length = 250)
    private String subAgencyName;

    @Column(name = "sub_agency_code", length = 50)
    private String subAgencyCode;

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