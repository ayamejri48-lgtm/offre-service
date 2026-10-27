package com.recrutement.offreservice.controller;

import com.recrutement.offreservice.service.CandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.recrutement.offreservice.dto.CandidatureCreateRequest;
import com.recrutement.offreservice.dto.CandidatureResponse;
import com.recrutement.offreservice.entity.Candidature;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.recrutement.offreservice.dto.CandidatureStatusUpdateRequest;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;

    @PostMapping
    public ResponseEntity<CandidatureResponse> postuler(
            @Valid @RequestBody CandidatureCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String role = jwt.getClaimAsString("role");

        if (!"CANDIDATE".equals(role)) {
            throw new AccessDeniedException(
                    "Seul un candidat peut postuler");
        }

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT");
        }

        Long candidateId = userIdClaim.longValue();

        Candidature candidature =
                candidatureService.postuler(
                        request,
                        candidateId,
                        "Bearer " + jwt.getTokenValue()
                );

        CandidatureResponse response = CandidatureResponse.builder()
                .id(candidature.getId())
                .offreId(request.getOffreId())
                .candidateId(candidature.getCandidateId())
                .cvId(candidature.getCvId())
                .status(candidature.getStatus())
                .appliedAt(candidature.getAppliedAt())
                .updatedAt(candidature.getUpdatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<CandidatureResponse>> getMesCandidatures(
            @AuthenticationPrincipal Jwt jwt) {

        if (!"CANDIDATE".equals(jwt.getClaimAsString("role"))) {
            throw new AccessDeniedException(
                    "Accès réservé aux candidats");
        }

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT");
        }

        Long candidateId = userIdClaim.longValue();

        List<CandidatureResponse> responses =
                candidatureService.getCandidaturesByCandidate(candidateId)
                        .stream()
                        .map(c -> CandidatureResponse.builder()
                                .id(c.getId())
                                .offreId(c.getOffre().getId())
                                .candidateId(c.getCandidateId())
                                .cvId(c.getCvId())
                                .status(c.getStatus())
                                .appliedAt(c.getAppliedAt())
                                .updatedAt(c.getUpdatedAt())
                                .build())
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<Void> retirerCandidature(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String role = jwt.getClaimAsString("role");

        if (!"CANDIDATE".equals(role)) {
            throw new AccessDeniedException(
                    "Seul un candidat peut retirer une candidature");
        }

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT");
        }

        Long candidateId = userIdClaim.longValue();

        candidatureService.retirerCandidature(id, candidateId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> modifierStatut(
            @PathVariable Long id,
            @Valid @RequestBody CandidatureStatusUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT");
        }

        Long userId = userIdClaim.longValue();
        String role = jwt.getClaimAsString("role");

        candidatureService.traiterCandidature(
                id,
                request.getStatus(),
                userId,
                role
        );

        return ResponseEntity.noContent().build();
    }



}