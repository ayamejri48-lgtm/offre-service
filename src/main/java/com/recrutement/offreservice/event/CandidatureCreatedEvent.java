package com.recrutement.offreservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatureCreatedEvent {

    private Long candidatureId;
    private Long offreId;
    private Long recruiterId;
    private Long candidateId;
}