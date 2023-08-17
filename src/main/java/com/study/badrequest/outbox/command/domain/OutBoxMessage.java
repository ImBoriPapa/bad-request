package com.study.badrequest.outbox.command.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OutBoxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxId;
    private String topic;
    private Long aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;

    @Builder
    public OutBoxMessage(String topic, Long aggregateId, String aggregateType, String eventType, String payload, LocalDateTime createdAt) {
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
    }
}
