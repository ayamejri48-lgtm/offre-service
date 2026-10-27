package com.recrutement.offreservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offres")
@Getter
@Setter
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recruiterId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private Integer experienceYears;

    private String educationLevel;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OffreStatus status = OffreStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}