package com.study.badrequest.api.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MemberCommandControllerTest {

    @InjectMocks
    MemberCommandController memberCommandController;
    @Test
    @DisplayName("회원가입 요청 태스트")
    void createMemberTest() throws Exception{
        //given

        //when

        //then

    }


}