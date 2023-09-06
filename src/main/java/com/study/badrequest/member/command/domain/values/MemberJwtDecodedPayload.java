package com.study.badrequest.member.command.domain.values;

import lombok.Getter;

@Getter
public class MemberJwtDecodedPayload {
    private final Long memberId;
    private final Authority authority;
    private final TokenStatus tokenStatus;

    public MemberJwtDecodedPayload(Long memberId, Authority authority, TokenStatus tokenStatus) {
        this.memberId = memberId;
        this.authority = authority;
        this.tokenStatus = tokenStatus;
    }
}
