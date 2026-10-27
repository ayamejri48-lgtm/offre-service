
package com.recrutement.offreservice.dto;

import com.recrutement.offreservice.entity.ContractType;
import com.recrutement.offreservice.entity.WorkMode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OffreUpdateRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "La localisation est obligatoire")
    private String location;

    @NotNull(message = "Le type de contrat est obligatoire")
    private ContractType contractType;

    @NotNull(message = "Le mode de travail est obligatoire")
    private WorkMode workMode;

    @NotNull(message = "L'expérience requise est obligatoire")
    @Min(value = 0)
    private Integer experienceYears;

    @NotBlank(message = "Le niveau d'études est obligatoire")
    private String educationLevel;

    @Future(message = "La date limite doit être dans le futur")
    private LocalDate deadline;
}
