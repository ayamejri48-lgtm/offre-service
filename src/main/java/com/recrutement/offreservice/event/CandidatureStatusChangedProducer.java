
package com.recrutement.offreservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidatureStatusChangedProducer {

    private static final String TOPIC = "candidature-status-changed-events";

    private final KafkaTemplate<String, CandidatureStatusChangedEvent> kafkaTemplate;

    public void sendStatusChangedEvent(CandidatureStatusChangedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.getCandidatureId().toString(),
                event
        );
    }
}
