
package com.recrutement.offreservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CandidatureStatusChangedKafkaListener {

    private final CandidatureStatusChangedProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusChanged(CandidatureStatusChangedEvent event) {
        producer.sendStatusChangedEvent(event);
    }
}
