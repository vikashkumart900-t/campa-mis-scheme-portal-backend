
package com.campa.mis.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "scheme_employment_generation", schema = "mis_scheme")
@Data
public class SchemeEmploymentGenerationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "labour_cost", precision = 15, scale = 2)
    private BigDecimal labourCost;

    @Column(name = "mandays_generated", precision = 15, scale = 2)
    private BigDecimal mandaysGenerated;

    @Column(name = "tribals")
    private Integer tribals;

    @Column(name = "forest_dwellers_benefitted")
    private Integer forestDwellersBenefitted;

    @Column(name = "created_at", updatable = false)
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
