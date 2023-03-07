package com.study.badrequest.domain.member.entity;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberTest {

    @Test
    @DisplayName("유저 네임 변경")
    void replaceUsernameTest() throws Exception{
        //given
        Member member = Member.createMember()
                .build();
        //when
        member.replaceUsername();
        //then
        log.info("member.getUsername()={} ",member.getUsername());
    }

}