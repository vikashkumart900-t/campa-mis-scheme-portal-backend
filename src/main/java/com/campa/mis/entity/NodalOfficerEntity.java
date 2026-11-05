package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "nodal_officers", schema = "mis_scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodalOfficerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nodal_officer_id")
    private Long nodalOfficerId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "officer_name", nullable = false, length = 150)
    private String officerName;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}