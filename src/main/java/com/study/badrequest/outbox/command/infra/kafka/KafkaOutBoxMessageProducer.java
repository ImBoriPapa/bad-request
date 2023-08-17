package com.study.badrequest.outbox.command.infra.kafka;

import com.study.badrequest.outbox.command.domain.OutBoxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaOutBoxMessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void produceEvent(OutBoxMessage outBoxMessage) {
        kafkaTemplate.send(outBoxMessage.getTopic(), outBoxMessage.getEventType(), outBoxMessage.getPayload());
    }
}
