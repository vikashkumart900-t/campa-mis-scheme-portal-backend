package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "organizations", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "organization_code", length = 50)
    private String organizationCode;

    @Column(name = "organization_name", nullable = false, length = 250)
    private String organizationName;

    @Column(name = "organization_type", nullable = false, length = 30)
    private String organizationType;

    @Column(name = "state_code")
    private Long stateCode;

    @Column(name = "district_code")
    private Long districtCode;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "ia_category", length = 50)
    private String iaCategory;


    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (this.submittedAt == null) {
            this.submittedAt = now;
        }
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}