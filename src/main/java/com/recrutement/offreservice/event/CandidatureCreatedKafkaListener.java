package com.recrutement.offreservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CandidatureCreatedKafkaListener {

    private final CandidatureEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCandidatureCreated(CandidatureCreatedEvent event) {
        producer.sendCandidatureCreatedEvent(event);
    }
}