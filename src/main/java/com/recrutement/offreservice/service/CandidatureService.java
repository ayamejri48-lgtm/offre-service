package com.recrutement.offreservice.service;

import com.recrutement.offreservice.repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.recrutement.offreservice.dto.CandidatureCreateRequest;
import com.recrutement.offreservice.entity.Candidature;
import com.recrutement.offreservice.entity.CandidatureStatus;
import com.recrutement.offreservice.entity.Offre;
import com.recrutement.offreservice.entity.OffreStatus;
import org.springframework.transaction.annotation.Transactional;
import com.recrutement.offreservice.repository.OffreRepository;
import com.recrutement.offreservice.client.UserServiceClient;
import com.recrutement.offreservice.dto.CvResponse;
import com.recrutement.offreservice.event.CandidatureCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.recrutement.offreservice.event.CandidatureStatusChangedEvent;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final OffreService offreService;
    private final OffreRepository offreRepository;
    private final UserServiceClient userServiceClient;
    private final ApplicationEventPublisher eventPublisher;




    @Transactional
    public Candidature postuler(
            CandidatureCreateRequest request,
            Long candidateId,
            String authorization) {



        Offre offre = offreRepository.findByIdForUpdate(request.getOffreId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Offre introuvable"));

        // Vérifier que l'offre accepte les candidatures
        if (offre.getStatus() != OffreStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Cette offre n'est pas ouverte aux candidatures"
            );
        }

        // Vérifier la date limite
        if (offre.getDeadline() != null
                && offre.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "La date limite de candidature est dépassée"
            );
        }

        // Empêcher les candidatures actives en double
        boolean dejaPostule =
                candidatureRepository.existsByOffreIdAndCandidateIdAndStatusNot(
                        offre.getId(),
                        candidateId,
                        CandidatureStatus.WITHDRAWN
                );

        if (dejaPostule) {
            throw new IllegalStateException(
                    "Vous avez déjà postulé à cette offre"
            );
        }

        List<CvResponse> cvs = userServiceClient.getMyCvs(authorization);

        boolean cvValide = cvs != null && cvs.stream()
                .anyMatch(cv ->
                        request.getCvId() != null
                                && request.getCvId().equals(cv.getId())
                                && Boolean.TRUE.equals(cv.getActive())
                                && Boolean.TRUE.equals(cv.getVisible())
                );

        if (!cvValide) {
            throw new IllegalStateException(
                    "Le CV sélectionné est introuvable, inactif ou non autorisé"
            );
        }

        Candidature candidature = new Candidature();
        candidature.setOffre(offre);
        candidature.setCandidateId(candidateId);
        candidature.setCvId(request.getCvId());
        candidature.setStatus(CandidatureStatus.PENDING);

        Candidature saved = candidatureRepository.save(candidature);

        eventPublisher.publishEvent(new CandidatureCreatedEvent(
                saved.getId(),
                offre.getId(),
                offre.getRecruiterId(),
                candidateId
        ));

        return saved;
    }



    // Consulter les candidatures d'un candidat


    // Consulter les candidatures d'un candidat
    @Transactional(readOnly = true)
    public List<Candidature> getCandidaturesByCandidate(Long candidateId) {
        return candidatureRepository.findByCandidateId(candidateId);
    }

    // Retirer sa candidature
    @Transactional
    public void retirerCandidature(Long candidatureId, Long candidateId) {

        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Candidature introuvable"));

        if (!candidature.getCandidateId().equals(candidateId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous ne pouvez pas retirer cette candidature");
        }

        if (candidature.getStatus() != CandidatureStatus.PENDING
                && candidature.getStatus() != CandidatureStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Cette candidature ne peut plus être retirée");
        }

        candidature.setStatus(CandidatureStatus.WITHDRAWN);
    }

    @Transactional(readOnly = true)
    public List<Candidature> getCandidaturesByOffre(
            Long offreId,
            Long userId,
            String role) {

        Offre offre = offreService.getOffreById(offreId);

        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à consulter ces candidatures");
        }

        return candidatureRepository.findByOffreId(offreId);
    }

    @Transactional
    public void traiterCandidature(
            Long candidatureId,
            CandidatureStatus nouveauStatut,
            Long userId,
            String role) {

        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Candidature introuvable"));

        Offre offre = candidature.getOffre();

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à traiter cette candidature");
        }

        // Vérifier le nouveau statut
        if (nouveauStatut != CandidatureStatus.UNDER_REVIEW
                && nouveauStatut != CandidatureStatus.ACCEPTED
                && nouveauStatut != CandidatureStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Statut de traitement invalide");
        }

        // Empêcher la modification d'une candidature finalisée ou retirée
        if (candidature.getStatus() == CandidatureStatus.ACCEPTED
                || candidature.getStatus() == CandidatureStatus.REJECTED
                || candidature.getStatus() == CandidatureStatus.WITHDRAWN) {
            throw new IllegalStateException(
                    "Cette candidature ne peut plus être modifiée");
        }

        CandidatureStatus ancienStatut = candidature.getStatus();

        if (ancienStatut == nouveauStatut) {
            return;
        }

        candidature.setStatus(nouveauStatut);

        eventPublisher.publishEvent(new CandidatureStatusChangedEvent(
                candidature.getId(),
                candidature.getCandidateId(),
                offre.getId(),
                ancienStatut.name(),
                nouveauStatut.name()
        ));
    }




}