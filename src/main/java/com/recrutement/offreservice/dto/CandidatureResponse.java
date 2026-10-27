package com.recrutement.offreservice.dto;

import com.recrutement.offreservice.entity.CandidatureStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CandidatureResponse {

    private Long id;
    private Long offreId;
    private Long candidateId;
    private Long cvId;
    private CandidatureStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}