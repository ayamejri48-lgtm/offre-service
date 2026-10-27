package com.recrutement.offreservice.specification;

import com.recrutement.offreservice.entity.ContractType;
import com.recrutement.offreservice.entity.Offre;
import com.recrutement.offreservice.entity.OffreStatus;
import com.recrutement.offreservice.entity.WorkMode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

public class OffreSpecification {

    private OffreSpecification() {
    }

    public static Specification<Offre> visiblePour(
            Long userId, String role) {

        if ("ADMIN".equals(role)) {
            return (root, query, cb) -> cb.conjunction();
        }

        if ("RECRUITER".equals(role)) {
            return (root, query, cb) -> cb.or(
                    cb.equal(root.get("status"), OffreStatus.PUBLISHED),
                    cb.equal(root.get("recruiterId"), userId)
            );
        }

        if ("CANDIDATE".equals(role)) {
            return (root, query, cb) ->
                    cb.equal(root.get("status"), OffreStatus.PUBLISHED);
        }

        throw new AccessDeniedException("Rôle non autorisé");
    }

    public static Specification<Offre> titreContient(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + title.trim().toLowerCase(java.util.Locale.ROOT) + "%"
            );
        };
    }

    public static Specification<Offre> localisationContient(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("location")),
                    "%" + location.trim().toLowerCase(java.util.Locale.ROOT) + "%"
            );
        };
    }

    public static Specification<Offre> typeContrat(ContractType contractType) {
        return (root, query, cb) -> {
            if (contractType == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("contractType"), contractType);
        };
    }

    public static Specification<Offre> modeTravail(WorkMode workMode) {
        return (root, query, cb) -> {
            if (workMode == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("workMode"), workMode);
        };
    }

    public static Specification<Offre> experienceMax(Integer maxYears) {
        return (root, query, cb) -> {
            if (maxYears == null) {
                return cb.conjunction();
            }

            if (maxYears < 0) {
                throw new IllegalArgumentException(
                        "L'expérience maximale ne peut pas être négative"
                );
            }

            return cb.or(
                    cb.isNull(root.get("experienceYears")),
                    cb.lessThanOrEqualTo(
                            root.<Integer>get("experienceYears"),
                            maxYears
                    )
            );
        };
    }

    public static Specification<Offre> niveauEtudes(String educationLevel) {
        return (root, query, cb) -> {
            if (educationLevel == null || educationLevel.isBlank()) {
                return cb.conjunction();
            }

            return cb.equal(
                    cb.lower(root.get("educationLevel")),
                    educationLevel.trim().toLowerCase(java.util.Locale.ROOT)
            );
        };
    }

    public static Specification<Offre> statut(OffreStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Offre> rechercher(
            Long userId,
            String role,
            String title,
            String location,
            ContractType contractType,
            WorkMode workMode,
            Integer maxExperience,
            String educationLevel,
            OffreStatus status) {

        return Specification.allOf(
                visiblePour(userId, role),
                titreContient(title),
                localisationContient(location),
                typeContrat(contractType),
                modeTravail(workMode),
                experienceMax(maxExperience),
                niveauEtudes(educationLevel),
                statut(status)
        );
    }
}