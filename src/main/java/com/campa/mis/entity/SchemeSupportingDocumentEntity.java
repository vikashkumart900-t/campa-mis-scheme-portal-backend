
package com.campa.mis.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "scheme_supporting_documents", schema = "mis_scheme")
@Data
public class SchemeSupportingDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    // KML or IMAGE
    @Column(name = "document_type", nullable = false, length = 20)
    private String documentType;

    @Column(name = "description", nullable = false,
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
