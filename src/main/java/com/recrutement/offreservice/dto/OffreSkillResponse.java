
package com.recrutement.offreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffreSkillResponse {

    private Long id;
    private Long offreId;
    private String name;
    private String level;
    private boolean required;
}
