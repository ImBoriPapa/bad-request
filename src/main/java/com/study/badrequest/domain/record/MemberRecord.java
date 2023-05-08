package com.study.badrequest.domain.record;


import com.study.badrequest.domain.member.Authority;
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
    @Column(name = "MEMBER_ID")
    private Long memberId;
    @Column(name = "MEMBER_EMAIL")
    private String memberEmail;
    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_AUTHORITY")
    private Authority memberAuthority;
    @Column(name = "IP_ADDRESS")
    private String ipAddress;
    @Column(name = "SPECIAL_NOTE")
    private String specialNote;
    @Column(name = "RECORDED_TIME")
    private LocalDateTime recodeTime;
    @Builder
    public MemberRecord(ActionStatus action, Long memberId, String memberEmail, Authority memberAuthority, String ipAddress, String specialNote, LocalDateTime recodeTime) {
        this.action = action;
        this.memberId = memberId;
        this.memberEmail = memberEmail;
        this.memberAuthority = memberAuthority;
        this.ipAddress = ipAddress;
        this.specialNote = specialNote;
        this.recodeTime = recodeTime;
    }




}
