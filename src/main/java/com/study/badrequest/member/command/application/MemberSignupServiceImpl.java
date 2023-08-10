package com.study.badrequest.member.command.application;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.*;
import com.study.badrequest.member.command.infra.uploader.ProfileImageUploader;
import com.study.badrequest.utils.email.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberSignupServiceImpl implements MemberSignupService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageUploader profileImageUploader;
    private final EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;

    /**
     * Create member Entity and do something
     *
     * @param form: SignupForm
     * @return Long: memberId;
     */
    @Override
    @Transactional
    public Long signupByEmail(SignupForm form) {
        log.info("Member SignUp By Email");

        nullCheck(form);

        final String convertedEmail = EmailUtils.convertDomainToLowercase(form.getEmail());
        final String encodedPassword = passwordEncoder.encode(form.getPassword());
        final String contact = form.getContact();
        final String nickname = form.getNickname();
        final String emailAuthenticationCode = form.getAuthenticationCode();

        emailDuplicateCheck(convertedEmail);

        authenticationCodeCheck(convertedEmail, emailAuthenticationCode);

        Member member = Member.createByEmail(convertedEmail, encodedPassword, contact, createMemberProfile(nickname));

        return persisteMember(member).getId();
    }

    private void authenticationCodeCheck(String convertedEmail, String emailAuthenticationCode) {
        EmailAuthenticationCode authenticationCode = emailAuthenticationCodeRepository.findByEmail(convertedEmail)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_AUTHENTICATION_EMAIL));

        boolean equals = authenticationCode.getCode().equals(emailAuthenticationCode);

        if (!equals) {
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_EMAIL_AUTHENTICATION_CODE);
        }

        if (LocalDateTime.now().isAfter(authenticationCode.getExpiredAt())) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_AUTHENTICATION_EMAIL);
        }
    }

    private void emailDuplicateCheck(String email) {
        boolean exists = memberRepository.findMembersByEmail(email).stream()
                .anyMatch(Member::isActive);

        if (exists) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_EMAIL);
        }
    }


    private MemberProfile createMemberProfile(String nickname) {
        ProfileImage defaultProfileImage = profileImageUploader.getDefaultProfileImage();
        return MemberProfile.createMemberProfile(nickname, defaultProfileImage);
    }

    private Member persisteMember(Member member) {
        return memberRepository.save(member);
    }

    private void nullCheck(SignupForm form) {

        final List<String> nullFields = new ArrayList<>();

        if (form.getEmail() == null) {
            nullFields.add("Null email");
        }

        if (form.getPassword() == null) {
            nullFields.add("Null password");
        }

        if (form.getNickname() == null) {
            nullFields.add("Null nickname");
        }

        if (form.getContact() == null) {
            nullFields.add("Null contact");
        }

        if (form.getAuthenticationCode() == null) {
            nullFields.add("Null authentication code");
        }

        if (form.getIpAddress() == null) {
            nullFields.add("Null IpAddress");
        }

        if (!nullFields.isEmpty()) {
            throwValidationErrorWithMessage("Null Fields: " + String.join(",", nullFields));
        }
    }

    private void throwValidationErrorWithMessage(String message) {
        throw CustomRuntimeException.createWithApiResponseStatusAndMessage(VALIDATION_ERROR, message);
    }

}
