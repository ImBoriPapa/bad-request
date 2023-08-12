package com.study.badrequest.question.command.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(of = "memberId")
public class MemberId {
    @Column(name = "MEMBER_ID")
    private Long memberId;

    public MemberId(Long memberId) {
        this.memberId = memberId;
    }
}
