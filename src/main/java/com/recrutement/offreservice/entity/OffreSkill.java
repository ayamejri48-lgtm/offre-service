package com.recrutement.offreservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "offre_skills")
@Getter
@Setter
public class OffreSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private boolean required = true;
}