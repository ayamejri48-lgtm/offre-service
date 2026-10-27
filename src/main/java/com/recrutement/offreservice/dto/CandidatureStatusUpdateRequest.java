package com.recrutement.offreservice.dto;

import com.recrutement.offreservice.entity.CandidatureStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidatureStatusUpdateRequest {

    @NotNull(message = "Le statut est obligatoire")
    private CandidatureStatus status;
}