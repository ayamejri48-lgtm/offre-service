package com.recrutement.offreservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidatureEventProducer {

    private static final String TOPIC = "candidature-created-events";

    private final KafkaTemplate<String, CandidatureCreatedEvent> kafkaTemplate;

    public void sendCandidatureCreatedEvent(CandidatureCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.getCandidatureId().toString(),
                event
        );
    }
}