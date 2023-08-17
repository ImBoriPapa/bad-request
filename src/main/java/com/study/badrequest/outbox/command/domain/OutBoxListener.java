package com.study.badrequest.outbox.command.domain;

import com.study.badrequest.outbox.command.infra.kafka.KafkaOutBoxMessageProducer;
import com.study.badrequest.outbox.command.infra.persistence.OutBoxMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OutBoxListener {

    private final OutBoxRepository outBoxRepository;
    private final KafkaOutBoxMessageProducer kafkaOutBoxMessageProducer;
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OutBoxEvent outBoxEvent) {
        log.info("Listen OutBox Event");

        OutBoxMessage outBoxMessage = OutBoxMessage.builder()
                .topic(outBoxEvent.getTopic())
                .aggregateId(outBoxEvent.getAggregateId())
                .aggregateType(outBoxEvent.getAggregateType())
                .eventType(outBoxEvent.getEventType())
                .payload(outBoxEvent.getPayload())
                .createdAt(LocalDateTime.now())
                .build();

        outBoxRepository.save(outBoxMessage);
    }

    @Scheduled(fixedDelay = 5000)
    public void polling() {
        List<OutBoxMessage> messages = outBoxRepository.findAll();
        List<OutBoxMessage> completedMessages = new ArrayList<>();

        for (OutBoxMessage message : messages) {
            log.info("send message");
            kafkaOutBoxMessageProducer.produceEvent(message);

            outBoxRepository.delete(message);
        }
    }
}
