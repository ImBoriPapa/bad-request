package com.study.badrequest.member.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.MemberPrincipal;
import com.study.badrequest.member.command.domain.Oauth2UserInformation;

import com.study.badrequest.member.command.domain.MemberEventDto;
import com.study.badrequest.exception.CustomOauth2LoginException;
import com.study.badrequest.member.command.domain.AccountStatus;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.RegistrationType;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.utils.email.EmailUtils;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.member.command.domain.RegistrationType.getOauth2UserInformation;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthUserDetailService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final S3ImageUploader imageUploader;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Oauth UserDetailService Load User");
        final OAuth2User oAuth2User;

        try {
            oAuth2User = super.loadUser(userRequest);
        } catch (OAuth2AuthorizationException exception) {
            log.error(exception.getError().getErrorCode());
            log.error(exception.getError().getDescription());
            throw new CustomOauth2LoginException(ApiResponseStatus.FAIL_GET_OAUTH2_USER_INFO);
        }

        Oauth2UserInformation oauth2UserInformation = getOauth2UserInformation(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        final String email = EmailUtils.convertDomainToLowercase(oauth2UserInformation.getEmail());

        List<Member> members = memberRepository.findMembersByEmail(email);
        return members.stream()
                .filter(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN)
                .findAny().map(member -> renewOauth2Member(oauth2UserInformation, member))
                .orElseGet(() -> createNewOauth2Member(oauth2UserInformation));
    }

    private MemberPrincipal createNewOauth2Member(Oauth2UserInformation oauth2UserInformation) {
        Member oauthMember = Member.createWithOauth2(
                oauth2UserInformation.getEmail(),
                oauth2UserInformation.getId(),
                getOauth2UserInformation(oauth2UserInformation.getProvider()));

        Member member = memberRepository.save(oauthMember);

        eventPublisher.publishEvent(new MemberEventDto.Create(member.getId(), oauth2UserInformation.getName(), "Oauth2 회원가입", "Oath2", member.getCreatedAt()));

        return new MemberPrincipal(member.getId(), member.getAuthenticationCode(), member.getAuthority().getAuthorities());
    }

    private MemberPrincipal renewOauth2Member(Oauth2UserInformation oauth2UserInformation, Member member) {

        if (member.getRegistrationType() == RegistrationType.BAD_REQUEST) {
            throw new CustomOauth2LoginException(ApiResponseStatus.ALREADY_REGISTERED_BY_EMAIL);
        }

        if (member.getRegistrationType() != getOauth2UserInformation(oauth2UserInformation.getProvider())) {
            throw new CustomOauth2LoginException(ApiResponseStatus.ALREADY_REGISTERED_BY_OAUTH2);
        }

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "Oauth2 로그인", "Oauth 접속", member.getUpdatedAt()));

        return new MemberPrincipal(member.getId(), member.getAuthenticationCode(), member.getAuthority().getAuthorities());
    }
}
