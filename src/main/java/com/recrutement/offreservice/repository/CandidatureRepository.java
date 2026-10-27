package com.recrutement.offreservice.repository;

import com.recrutement.offreservice.entity.Candidature;
import com.recrutement.offreservice.entity.CandidatureStatus;
import com.recrutement.offreservice.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    // Candidatures d'un candidat
    List<Candidature> findByCandidateId(Long candidateId);

    // Candidatures pour une offre
    List<Candidature> findByOffreId(Long offreId);

    // Vérifier l'existence d'une candidature non retirée
    boolean existsByOffreIdAndCandidateIdAndStatusNot(
            Long offreId,
            Long candidateId,
            CandidatureStatus status
    );

}