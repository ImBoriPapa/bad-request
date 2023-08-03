package com.study.badrequest.service.member;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreateMemberProfileTest extends MemberProfileServiceTestBase {

    @Test
    @DisplayName("회원 프로필 생성 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        Long memberId = 123L;
        String nickname = "닉네임";
        //when
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> memberProfileService.createMemberProfileProcessing(memberId, nickname))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());

    }

    @Test
    @DisplayName("회원 프로필 생성 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void test2() throws Exception {
        //given
        Long memberId = 123L;
        String nickname = "닉네임";
        //when
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> memberProfileService.createMemberProfileProcessing(memberId, nickname))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());

    }

}