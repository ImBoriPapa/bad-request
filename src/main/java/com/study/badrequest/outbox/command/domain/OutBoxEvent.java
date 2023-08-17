package com.study.badrequest.outbox.command.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OutBoxEvent {
    private String topic;
    private Long aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;

    @Builder
    public OutBoxEvent(String topic, Long aggregateId, String aggregateType, String eventType, String payload) {
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
    }
}
