
package com.recrutement.offreservice.repository;

import com.recrutement.offreservice.entity.OffreSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffreSkillRepository extends JpaRepository<OffreSkill, Long> {

    // Récupérer les compétences d'une offre
    List<OffreSkill> findByOffreId(Long offreId);

    // Vérifier si une compétence existe déjà dans une offre
    boolean existsByOffreIdAndNameIgnoreCase(Long offreId, String name);

    // Vérifier les doublons lors d'une modification
    boolean existsByOffreIdAndNameIgnoreCaseAndIdNot(
            Long offreId,
            String name,
            Long id
    );
}
