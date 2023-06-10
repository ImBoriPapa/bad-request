package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.RefreshToken;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailLoginTest extends LoginServiceTestBase {

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void 이메일로그인테스트1() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 활동 중인 회원 정보가 1개 이상일 경우")
    void 이메일로그인테스트2() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member1 = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        Member member2 = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));

        List<Member> members = List.of(member1, member2);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.FOUND_ACTIVE_MEMBERS_WITH_DUPLICATE_EMAILS.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 활동 중인 회원 정보가 없을 경우")
    void 이메일로그인테스트3() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.THIS_IS_NOT_REGISTERED_AS_MEMBER.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: Oauth2로 가입된 회원일 경우")
    void 이메일로그인테스트4() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        String oauthId = "12345";
        Member oauth2Member = Member.createMemberWithOauth(requestedEmail, oauthId, RegistrationType.GOOGLE, new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(List.of(oauth2Member));

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ALREADY_REGISTERED_BY_OAUTH2.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 회원의 이메일 인증이 필요한 경우")
    void 이메일로그인테스트5() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        member.changeStatus(AccountStatus.REQUIRED_MAIL_CONFIRMED);

        List<Member> members = List.of(member);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.IS_NOT_CONFIRMED_MAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 임시 비밀번호가 발급된 상태에서 로그인, 임시 비밀번호 정보가 없을 경우")
    void 이메일로그인테스트6() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        member.changeStatus(AccountStatus.PASSWORD_IS_TEMPORARY);

        List<Member> members = List.of(member);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.findByMember(member)).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOT_FOUND_TEMPORARY_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 임시 비밀번호가 발급된 상태에서 로그인, 임시 비밀번호 정보가 만료")
    void 이메일로그인테스트7() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        member.changeStatus(AccountStatus.PASSWORD_IS_TEMPORARY);
        List<Member> members = List.of(member);

        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword(password, member);
        temporaryPassword.changeExpiredAt(LocalDateTime.now().minusHours(24));

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.findByMember(member)).willReturn(Optional.of(temporaryPassword));
        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.IS_EXPIRED_TEMPORARY_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 임시 비밀번호가 발급된 상태에서 로그인, 임시 비밀번호가 불일치")
    void 이메일로그인테스트8() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        member.changeStatus(AccountStatus.PASSWORD_IS_TEMPORARY);
        List<Member> members = List.of(member);

        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword(password, member);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.findByMember(member)).willReturn(Optional.of(temporaryPassword));
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 실패 테스트: 비밀번호 불일치")
    void 이메일로그인테스트9() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        List<Member> members = List.of(member);

        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword(password, member);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.findByMember(member)).willReturn(Optional.of(temporaryPassword));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        //then
        assertThatThrownBy(() -> loginService.emailLogin(requestedEmail, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 로그인 성공 테스트")
    void 이메일로그인테스트10() throws Exception {
        //given
        String requestedEmail = "email@email.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";

        Member member = Member.createMemberWithEmail(requestedEmail, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        List<Member> members = List.of(member);

        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword(password, member);

        JwtTokenDto tokenDto = JwtTokenDto.builder()
                .accessToken("accessToken")
                .accessTokenExpiredAt(LocalDateTime.now().plusMinutes(10))
                .refreshToken("refreshToken")
                .refreshTokenExpirationMill(360000L)
                .build();

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .changeableId(member.getChangeableId())
                .memberId(1L)
                .token(tokenDto.getRefreshToken())
                .authority(Authority.MEMBER)
                .expiration(tokenDto.getRefreshTokenExpirationMill())
                .build();

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.findByMember(member)).willReturn(Optional.of(temporaryPassword));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(jwtUtils.generateJwtTokens(any())).willReturn(tokenDto);
        given(redisRefreshTokenRepository.save(any())).willReturn(refreshToken);
        loginService.emailLogin(requestedEmail, password, ipAddress);
        //then
        verify(memberRepository).findMembersByEmail(requestedEmail);
        verify(temporaryPasswordRepository).findByMember(member);
        verify(passwordEncoder).matches(any(), any());
        verify(passwordEncoder).matches(any(), any());
        verify(jwtUtils).generateJwtTokens(member.getChangeableId());
        verify(redisRefreshTokenRepository).save(any());
        verify(eventPublisher).publishEvent(new MemberEventDto.Login(any(), "이메일 로그인", ipAddress, LocalDateTime.now()));

    }
}
