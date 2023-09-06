package com.study.badrequest.member.command.domain.values;

import lombok.Getter;

@Getter
public class MemberJwtEncodedPayload {
    private final String memberId;
    private final String authority;
    private final String status;

    public MemberJwtEncodedPayload(String memberId, String authority, String status) {
        this.memberId = memberId;
        this.authority = authority;
        this.status = status;
    }
}
