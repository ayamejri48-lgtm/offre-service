
package com.recrutement.offreservice.dto;

import com.recrutement.offreservice.entity.ContractType;
import com.recrutement.offreservice.entity.WorkMode;
import com.recrutement.offreservice.entity.OffreStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffreResponse {

    private Long id;
    private Long recruiterId;
    private String title;
    private String description;
    private String location;
    private ContractType contractType;
    private WorkMode workMode;
    private Integer experienceYears;
    private String educationLevel;
    private LocalDate deadline;
    private OffreStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
