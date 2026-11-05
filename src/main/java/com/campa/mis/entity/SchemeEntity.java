package com.campa.mis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "schemes", schema = "mis_scheme")
@Getter
@Setter
public class SchemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id")
    private Long schemeId;

    @Column(name = "scheme_name")
    private String schemeName;

    @Column(name = "scheme_status")
    private String schemeStatus;

    @Column(name = "starting_year")
    private Short startingYear;

    @Column(name = "project_period")
    private String projectPeriod;

    @Column(name = "fund_released", precision = 19, scale = 2)
    private BigDecimal fundReleased;

    @Column(name = "fund_utilised", precision = 19, scale = 2)
    private BigDecimal fundUtilised;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "program_division_id")
    private Long programDivisionId;

    @Column(name = "organization_id")
    private Long organizationId;


    @Column(name = "project_cost", precision = 19, scale = 2)
    private BigDecimal projectCost;

    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name="scheme_type")
    private String schemeType;
    
    
}