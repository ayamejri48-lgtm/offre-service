package com.recrutement.offreservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures")
@Getter
@Setter
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Long cvId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidatureStatus status = CandidatureStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        updatedAt = appliedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}