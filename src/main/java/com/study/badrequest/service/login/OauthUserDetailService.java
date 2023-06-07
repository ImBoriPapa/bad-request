package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.MemberPrincipal;
import com.study.badrequest.domain.login.Oauth2UserInformation;
import com.study.badrequest.domain.member.RegistrationType;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;

import com.study.badrequest.exception.CustomOauth2LoginException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.study.badrequest.domain.member.RegistrationType.getOauth2UserInformation;


@Component
@RequiredArgsConstructor
@Slf4j
public class OauthUserDetailService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final S3ImageUploader imageUploader;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Oauth UserDetailService Load User");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Oauth2UserInformation oauth2UserInformation = getOauth2UserInformation(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        Optional<Member> optionalMember = memberRepository.findByEmail(
                oauth2UserInformation.getEmail());

        return optionalMember.map(member -> renewOauth2Member(oauth2UserInformation, member)).orElseGet(() -> createNewOauth2Member(oauth2UserInformation));

    }

    private MemberPrincipal createNewOauth2Member(Oauth2UserInformation oauth2UserInformation) {
        Member oauthMember = Member.createMemberWithOauth(
                oauth2UserInformation.getEmail(),
                oauth2UserInformation.getId(),
                getOauth2UserInformation(oauth2UserInformation.getProvider()),
                new MemberProfile(oauth2UserInformation.getName(), ProfileImage.createDefaultImage(imageUploader.DEFAULT_PROFILE_IMAGE))
        );

        Member member = memberRepository.save(oauthMember);
//        eventPublisher.publishEvent(new MemberEventDto.Create(member, "Oauth2 회원가입"));
        return new MemberPrincipal(member.getId(), member.getChangeableId(), member.getAuthority().getAuthorities());
    }

    private MemberPrincipal renewOauth2Member(Oauth2UserInformation oauth2UserInformation, Member member) {

        if (member.getRegistrationType() == RegistrationType.BAD_REQUEST) {
            throw new CustomOauth2LoginException(ApiResponseStatus.ALREADY_REGISTERED_SELF_LOGIN_EMAIL);
        }

        if (member.getRegistrationType() != getOauth2UserInformation(oauth2UserInformation.getProvider())) {
            throw new CustomOauth2LoginException(ApiResponseStatus.ALREADY_REGISTERED_OAUTH2_EMAIL);
        }

        if (member.updateOauthMember(oauth2UserInformation.getId(), oauth2UserInformation.getName())) {
//            eventPublisher.publishEvent(new MemberEventDto.Update(member, "Oauth2 닉네임 변경"));
        }

        return new MemberPrincipal(member.getId(), member.getChangeableId(), member.getAuthority().getAuthorities());
    }
}
