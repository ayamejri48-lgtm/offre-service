
package com.recrutement.offreservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CandidatureCreateRequest {

    @NotNull(message = "L'identifiant de l'offre est obligatoire")
    @Positive(message = "L'identifiant de l'offre doit être positif")
    private Long offreId;

    @NotNull(message = "L'identifiant du CV est obligatoire")
    @Positive(message = "L'identifiant du CV doit être positif")
    private Long cvId;
}
