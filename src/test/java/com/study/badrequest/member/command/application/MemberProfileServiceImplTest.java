package com.study.badrequest.member.command.application;


import com.study.badrequest.member.command.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberProfileServiceImplTest {

    private MemberProfileService memberProfileService;
    private MemberRepository memberRepository;

    @BeforeEach
    void before() {
        memberRepository = new FakeMemberRepository();
        memberProfileService = new MemberProfileServiceImpl(memberRepository);
    }


}