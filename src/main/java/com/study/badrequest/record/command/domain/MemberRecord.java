package com.study.badrequest.record.command.domain;


import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Getter
public class MemberRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_RECORD_ID")
    private Long id;
    @Column(name = "ACTION")
    @Enumerated(EnumType.STRING)
    private ActionStatus action;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity member;
    @Column(name = "IP_ADDRESS")
    private String ipAddress;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "RECORDED_TIME")
    private LocalDateTime recodeTime;

    @Builder
    public MemberRecord(ActionStatus action, MemberEntity member, String ipAddress, String description, LocalDateTime recodeTime) {
        this.action = action;
        this.member = member;
        this.ipAddress = ipAddress;
        this.description = description;
        this.recodeTime = recodeTime;
    }


}
