
package com.recrutement.offreservice.controller;

import com.recrutement.offreservice.dto.OffreSkillRequest;
import com.recrutement.offreservice.dto.OffreSkillResponse;
import com.recrutement.offreservice.service.OffreSkillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offres/{offreId}/skills")
@RequiredArgsConstructor
public class OffreSkillController {

    private final OffreSkillService offreSkillService;


    @PostMapping
    public ResponseEntity<Void> addSkill(
            @PathVariable Long offreId,
            @Valid @RequestBody OffreSkillRequest request,
            @AuthenticationPrincipal Jwt jwt) {



        Long userId = getUserId(jwt);
        String role = jwt.getClaimAsString("role");

        offreSkillService.addSkill(offreId, request, userId, role);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping
    public ResponseEntity<List<OffreSkillResponse>> getSkillsByOffre(
            @PathVariable Long offreId,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = getUserId(jwt);
        String role = jwt.getClaimAsString("role");

        List<OffreSkillResponse> skills =
                offreSkillService.getSkillsByOffre(offreId, userId, role)
                        .stream()
                        .map(skill -> OffreSkillResponse.builder()
                                .id(skill.getId())
                                .offreId(skill.getOffre().getId())
                                .name(skill.getName())
                                .level(skill.getLevel())
                                .required(skill.isRequired())
                                .build())
                        .toList();

        return ResponseEntity.ok(skills);
    }



    @PutMapping("/{skillId}")
    public ResponseEntity<Void> updateSkill(
            @PathVariable Long offreId,
            @PathVariable Long skillId,
            @Valid @RequestBody OffreSkillRequest request,
            @AuthenticationPrincipal Jwt jwt) {


        Long userId = getUserId(jwt);
        String role = jwt.getClaimAsString("role");

        offreSkillService.updateSkill(
                offreId, skillId, request, userId, role
        );

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long offreId,
            @PathVariable Long skillId,
            @AuthenticationPrincipal Jwt jwt) {



        Long userId = getUserId(jwt);
        String role = jwt.getClaimAsString("role");

        offreSkillService.deleteSkill(
                offreId, skillId, userId, role
        );

        return ResponseEntity.noContent().build();
    }



    private Long getUserId(Jwt jwt) {

        Number userIdClaim = jwt.getClaim("userId");

        if (userIdClaim == null) {
            throw new AccessDeniedException(
                    "Identifiant utilisateur absent du JWT"
            );
        }

        return userIdClaim.longValue();
    }


}
