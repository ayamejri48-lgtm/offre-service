
package com.recrutement.offreservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatureStatusChangedEvent {

    private Long candidatureId;
    private Long candidateId;
    private Long offreId;
    private String oldStatus;
    private String newStatus;
}
