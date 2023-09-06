package com.study.badrequest.utils.jwt;

import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.member.command.domain.values.Authority;
import com.study.badrequest.member.command.domain.values.MemberJwtDecodedPayload;
import com.study.badrequest.member.command.domain.values.MemberJwtEncodedPayload;
import com.study.badrequest.member.command.domain.values.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtMemberJwtEncodedPayloadEncoderTest {
    private final JwtPayloadEncoder jwtPayloadEncoder;

    public JwtMemberJwtEncodedPayloadEncoderTest() {
        this.jwtPayloadEncoder = new JwtPayloadEncoder();
    }

    @Test
    @DisplayName("암호화")
    void 암호화_테스트() throws Exception{
        //given
        String memberId = new MemberId(123L).getId().toString();
        Authority authority = Authority.MEMBER;
        TokenStatus tokenStatus = TokenStatus.ACTIVE;

        //when
        MemberJwtEncodedPayload encodedPayload = jwtPayloadEncoder.encodedPayload(memberId, authority, tokenStatus);
        MemberJwtDecodedPayload decodedPayload = jwtPayloadEncoder.decodedPayload(encodedPayload);
        //then
        System.out.println(encodedPayload.getMemberId());
        System.out.println(decodedPayload.getMemberId());
    }

}