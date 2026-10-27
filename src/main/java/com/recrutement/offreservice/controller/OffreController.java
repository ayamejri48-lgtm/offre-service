package com.recrutement.offreservice.controller;

import com.recrutement.offreservice.dto.OffreCreateRequest;
import com.recrutement.offreservice.service.OffreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.recrutement.offreservice.dto.OffreResponse;
import com.recrutement.offreservice.entity.Offre;
import com.recrutement.offreservice.entity.OffreStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import com.recrutement.offreservice.dto.OffreUpdateRequest;
import com.recrutement.offreservice.entity.ContractType;
import com.recrutement.offreservice.entity.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/offres")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;



    @GetMapping("/{id}")
    public ResponseEntity<OffreResponse> getOffreById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Offre offre = offreService.getOffreById(id);

        String role = jwt.getClaimAsString("role");
        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();

        // Vérifier les permissions
        boolean isAdmin = "ADMIN".equals(role);

        boolean isOwner = "RECRUITER".equals(role)
                && offre.getRecruiterId().equals(userId);

        boolean isPublished = offre.getStatus() == OffreStatus.PUBLISHED;

        if (!isAdmin && !isOwner && !isPublished) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas autorisé à consulter cette offre"
            );
        }

        // Construire la réponse
        OffreResponse response = OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build();

        return ResponseEntity.ok(response);
    }




    @GetMapping
    public ResponseEntity<List<OffreResponse>> getAllOffres(
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        List<OffreResponse> responses = offreService
                .getVisibleOffres(userId, role)
                .stream()
                .map(offre -> OffreResponse.builder()
                        .id(offre.getId())
                        .recruiterId(offre.getRecruiterId())
                        .title(offre.getTitle())
                        .description(offre.getDescription())
                        .location(offre.getLocation())
                        .contractType(offre.getContractType())
                        .workMode(offre.getWorkMode())
                        .experienceYears(offre.getExperienceYears())
                        .educationLevel(offre.getEducationLevel())
                        .deadline(offre.getDeadline())
                        .status(offre.getStatus())
                        .createdAt(offre.getCreatedAt())
                        .updatedAt(offre.getUpdatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }


    @PostMapping
    public ResponseEntity<OffreResponse> createOffre(
            @Valid @RequestBody OffreCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long recruiterId = userIdClaim.longValue();

        Offre offre = offreService.createOffre(request, recruiterId);

        OffreResponse response = OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<OffreResponse> updateOffre(
            @PathVariable Long id,
            @Valid @RequestBody OffreUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        Offre offre = offreService.updateOffre(
                id, request, userId, role
        );

        OffreResponse response = OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build();

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        offreService.deleteOffre(id, userId, role);

        return ResponseEntity.noContent().build();
    }



    @PatchMapping("/{id}/publish")
    public ResponseEntity<OffreResponse> publishOffre(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        Offre offre = offreService.publishOffre(id, userId, role);

        OffreResponse response = OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build();

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/close")
    public ResponseEntity<OffreResponse> closeOffre(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        Offre offre = offreService.closeOffre(id, userId, role);

        OffreResponse response = OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<OffreResponse>> searchOffres(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ContractType contractType,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) OffreStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Pagination invalide : page >= 0 et size entre 1 et 100"
            );
        }

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<OffreResponse> result = offreService.rechercherOffres(
                userId,
                role,
                title,
                location,
                contractType,
                workMode,
                maxExperience,
                educationLevel,
                status,
                pageable
        ).map(offre -> OffreResponse.builder()
                .id(offre.getId())
                .recruiterId(offre.getRecruiterId())
                .title(offre.getTitle())
                .description(offre.getDescription())
                .location(offre.getLocation())
                .contractType(offre.getContractType())
                .workMode(offre.getWorkMode())
                .experienceYears(offre.getExperienceYears())
                .educationLevel(offre.getEducationLevel())
                .deadline(offre.getDeadline())
                .status(offre.getStatus())
                .createdAt(offre.getCreatedAt())
                .updatedAt(offre.getUpdatedAt())
                .build());

        return ResponseEntity.ok(result);
    }



}