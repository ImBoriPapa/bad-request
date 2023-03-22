package com.study.badrequest.domain.record.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
public class MemberRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_RECORD_ID")
    private Long id;
    @Column(name = "MEMBER_ID")
    private Long memberId;
    @Column(name = "ACTION")
    private ActionStatus action;
    @Enumerated(EnumType.STRING)
    private LocalDateTime recodeTime;

    public MemberRecord(Long memberId, ActionStatus action) {
        this.memberId = memberId;
        this.action = action;
        this.recodeTime = LocalDateTime.now();
    }
}
