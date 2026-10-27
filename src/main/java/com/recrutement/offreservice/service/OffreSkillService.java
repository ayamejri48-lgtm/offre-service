
package com.recrutement.offreservice.service;

import com.recrutement.offreservice.dto.OffreSkillRequest;
import com.recrutement.offreservice.entity.Offre;
import com.recrutement.offreservice.entity.OffreSkill;
import com.recrutement.offreservice.entity.OffreStatus;
import com.recrutement.offreservice.repository.OffreSkillRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreSkillService {

    private final OffreSkillRepository offreSkillRepository;
    private final OffreService offreService;

    @Transactional
    public OffreSkill addSkill(
            Long offreId,
            OffreSkillRequest request,
            Long userId,
            String role) {

        Offre offre = offreService.getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à modifier les compétences de cette offre"
            );
        }

        // Interdire les modifications d'une offre clôturée
        if (offre.getStatus() == OffreStatus.CLOSED) {
            throw new IllegalStateException(
                    "Impossible de modifier les compétences d'une offre clôturée"
            );
        }

        // Vérifier les doublons
        String skillName = request.getName().trim();

        if (offreSkillRepository.existsByOffreIdAndNameIgnoreCase(
                offreId, skillName)) {
            throw new IllegalStateException(
                    "Cette compétence existe déjà dans l'offre"
            );
        }

        // Créer la compétence
        OffreSkill skill = new OffreSkill();
        skill.setOffre(offre);
        skill.setName(skillName);
        skill.setLevel(request.getLevel().trim());
        skill.setRequired(request.getRequired());

        return offreSkillRepository.save(skill);
    }


    @Transactional(readOnly = true)
    public List<OffreSkill> getSkillsByOffre(
            Long offreId,
            Long userId,
            String role) {

        Offre offre = offreService.getOffreById(offreId);

        // Vérifier les permissions de consultation
        boolean isAdmin = "ADMIN".equals(role);

        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        boolean isPublished = offre.getStatus() == OffreStatus.PUBLISHED;

        if (!isAdmin && !isOwner && !isPublished) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à consulter les compétences de cette offre"
            );
        }

        return offreSkillRepository.findByOffreId(offreId);
    }


    @Transactional
    public OffreSkill updateSkill(
            Long offreId,
            Long skillId,
            OffreSkillRequest request,
            Long userId,
            String role) {

        Offre offre = offreService.getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à modifier cette compétence"
            );
        }

        // Interdire la modification d'une offre clôturée
        if (offre.getStatus() == OffreStatus.CLOSED) {
            throw new IllegalStateException(
                    "Impossible de modifier les compétences d'une offre clôturée"
            );
        }

        // Rechercher la compétence
        OffreSkill skill = offreSkillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Compétence introuvable avec l'ID : " + skillId
                ));

        // Vérifier que la compétence appartient à l'offre
        if (!skill.getOffre().getId().equals(offreId)) {
            throw new IllegalArgumentException(
                    "Cette compétence n'appartient pas à cette offre"
            );
        }

        // Vérifier les doublons
        String skillName = request.getName().trim();

        if (offreSkillRepository.existsByOffreIdAndNameIgnoreCaseAndIdNot(
                offreId, skillName, skillId)) {
            throw new IllegalStateException(
                    "Cette compétence existe déjà dans l'offre"
            );
        }

        // Mettre à jour la compétence
        skill.setName(skillName);
        skill.setLevel(request.getLevel().trim());
        skill.setRequired(request.getRequired());

        return offreSkillRepository.save(skill);
    }



    @Transactional
    public void deleteSkill(
            Long offreId,
            Long skillId,
            Long userId,
            String role) {

        Offre offre = offreService.getOffreById(offreId);

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à supprimer cette compétence"
            );
        }

        // Interdire les modifications d'une offre clôturée
        if (offre.getStatus() == OffreStatus.CLOSED) {
            throw new IllegalStateException(
                    "Impossible de supprimer une compétence d'une offre clôturée"
            );
        }

        // Rechercher la compétence
        OffreSkill skill = offreSkillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Compétence introuvable avec l'ID : " + skillId
                ));

        // Vérifier que la compétence appartient à l'offre
        if (!skill.getOffre().getId().equals(offreId)) {
            throw new IllegalArgumentException(
                    "Cette compétence n'appartient pas à cette offre"
            );
        }

        // Supprimer la compétence
        offreSkillRepository.delete(skill);
    }

}
