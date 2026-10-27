
package com.recrutement.offreservice.service;

import com.recrutement.offreservice.entity.Offre;
import com.recrutement.offreservice.repository.CandidatureRepository;
import com.recrutement.offreservice.repository.OffreRepository;
import com.recrutement.offreservice.repository.OffreSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.recrutement.offreservice.dto.OffreCreateRequest;
import com.recrutement.offreservice.entity.OffreStatus;
import org.springframework.transaction.annotation.Transactional;
import com.recrutement.offreservice.dto.OffreUpdateRequest;
import org.springframework.security.access.AccessDeniedException;
import com.recrutement.offreservice.specification.OffreSpecification;
import com.recrutement.offreservice.entity.ContractType;
import com.recrutement.offreservice.entity.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository offreRepository;
    private final OffreSkillRepository offreSkillRepository;
    private final CandidatureRepository candidatureRepository;

    // Récupérer une offre par ID
    public Offre getOffreById(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Offre introuvable avec l'ID : " + id
                        ));
    }

    // Récupérer toutes les offres
    public List<Offre> getAllOffres() {
        return offreRepository.findAll();
    }

    // Récupérer les offres d'un recruteur
    public List<Offre> getOffresByRecruiter(Long recruiterId) {
        return offreRepository.findByRecruiterId(recruiterId);
    }




    @Transactional
    public Offre createOffre(OffreCreateRequest request, Long recruiterId) {

        Offre offre = new Offre();

        offre.setRecruiterId(recruiterId);
        offre.setTitle(request.getTitle());
        offre.setDescription(request.getDescription());
        offre.setLocation(request.getLocation());
        offre.setContractType(request.getContractType());
        offre.setWorkMode(request.getWorkMode());
        offre.setExperienceYears(request.getExperienceYears());
        offre.setEducationLevel(request.getEducationLevel());
        offre.setDeadline(request.getDeadline());
        offre.setStatus(OffreStatus.DRAFT);

        return offreRepository.save(offre);
    }



    @Transactional
    public Offre updateOffre(
            Long offreId,
            OffreUpdateRequest request,
            Long userId,
            String role) {

        Offre offre = getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à modifier cette offre"
            );
        }

        // Une offre clôturée ne peut pas être modifiée
        if (offre.getStatus() == OffreStatus.CLOSED) {
            throw new IllegalStateException(
                    "Une offre clôturée ne peut pas être modifiée"
            );
        }

        // Mise à jour des informations
        offre.setTitle(request.getTitle());
        offre.setDescription(request.getDescription());
        offre.setLocation(request.getLocation());
        offre.setContractType(request.getContractType());
        offre.setWorkMode(request.getWorkMode());
        offre.setExperienceYears(request.getExperienceYears());
        offre.setEducationLevel(request.getEducationLevel());
        offre.setDeadline(request.getDeadline());

        return offreRepository.save(offre);
    }



    @Transactional
    public void deleteOffre(Long offreId, Long userId, String role) {

        Offre offre = getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à supprimer cette offre"
            );
        }

        // Seuls les brouillons peuvent être supprimés
        if (offre.getStatus() != OffreStatus.DRAFT) {
            throw new IllegalStateException(
                    "Seules les offres en brouillon peuvent être supprimées"
            );
        }

        // Vérifier les candidatures associées
        if (!candidatureRepository.findByOffreId(offreId).isEmpty()) {
            throw new IllegalStateException(
                    "Impossible de supprimer une offre contenant des candidatures"
            );
        }

        // Supprimer les compétences associées
        offreSkillRepository.deleteAll(
                offreSkillRepository.findByOffreId(offreId)
        );

        offreRepository.delete(offre);
    }




    @Transactional
    public Offre publishOffre(Long offreId, Long userId, String role) {

        Offre offre = getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à publier cette offre"
            );
        }

        // Seul un brouillon peut être publié
        if (offre.getStatus() != OffreStatus.DRAFT) {
            throw new IllegalStateException(
                    "Seule une offre en brouillon peut être publiée"
            );
        }

        // Vérifier la date limite
        if (offre.getDeadline() != null
                && !offre.getDeadline().isAfter(LocalDate.now())) {
            throw new IllegalStateException(
                    "La date limite de candidature est dépassée"
            );
        }

        offre.setStatus(OffreStatus.PUBLISHED);

        return offreRepository.save(offre);
    }




    @Transactional
    public Offre closeOffre(Long offreId, Long userId, String role) {

        Offre offre = getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à clôturer cette offre"
            );
        }

        // Seule une offre publiée peut être clôturée
        if (offre.getStatus() != OffreStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Seule une offre publiée peut être clôturée"
            );
        }

        offre.setStatus(OffreStatus.CLOSED);

        return offreRepository.save(offre);
    }


    public List<Offre> getPublishedOffres() {
        return offreRepository.findByStatus(OffreStatus.PUBLISHED);
    }

    public List<Offre> getVisibleOffres(Long userId, String role) {

        if ("ADMIN".equals(role)) {
            return offreRepository.findAll();
        }

        if ("RECRUITER".equals(role)) {
            return offreRepository.findAll().stream()
                    .filter(offre ->
                            offre.getStatus() == OffreStatus.PUBLISHED
                                    || offre.getRecruiterId().equals(userId)
                    )
                    .toList();
        }

        return getPublishedOffres();
    }

    @Transactional(readOnly = true)
    public Page<Offre> rechercherOffres(
            Long userId,
            String role,
            String title,
            String location,
            ContractType contractType,
            WorkMode workMode,
            Integer maxExperience,
            String educationLevel,
            OffreStatus status,
            Pageable pageable) {

        return offreRepository.findAll(
                OffreSpecification.rechercher(
                        userId,
                        role,
                        title,
                        location,
                        contractType,
                        workMode,
                        maxExperience,
                        educationLevel,
                        status
                ),
                pageable
        );
    }




}
