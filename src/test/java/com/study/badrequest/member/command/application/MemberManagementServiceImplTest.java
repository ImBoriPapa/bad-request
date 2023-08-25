package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.TestMemberPasswordEncoder;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.imports.ProfileImageUploader;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberManagementServiceImplTest {

    private MemberManagementService memberManagementService;
    private MemberRepository memberRepository;
    private MemberPasswordEncoder memberPasswordEncoder;
    private ProfileImageUploader profileImageUploader;
    private AuthenticationCodeGenerator authenticationCodeGenerator;


    public MemberManagementServiceImplTest() {
        this.memberPasswordEncoder = new TestMemberPasswordEncoder();
        this.memberManagementService = new MemberManagementServiceImpl(memberRepository, memberPasswordEncoder, profileImageUploader, authenticationCodeGenerator);
    }

    @Test
    @DisplayName("")
    void 회원생성() throws Exception {
        //given

        //when

        //then

    }

}