
package com.recrutement.offreservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OffreSkillRequest {

    @NotBlank(message = "Le nom de la compétence est obligatoire")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Le niveau de compétence est obligatoire")
    private String level;

    @NotNull(message = "Le caractère obligatoire est requis")
    private Boolean required;
}
