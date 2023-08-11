package com.study.badrequest.member.command.application;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.application.dto.SignupForm;
import com.study.badrequest.member.command.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberSignupServiceImpl implements MemberSignupService {
    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder memberPasswordEncoder;
    private final ProfileImageUploader profileImageUploader;
    private final EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;

    /**
     * 이메일로 회원 가입하는 메서드입니다.
     *
     * @param form (SignupForm, required)
     * @return Long 회원 식별 아이디
     * @ImplNote 요청에 필요한 필드에 null,blank 를 확인합니다.
     * 이메일 인증 코드를 찾고 없을시 예외를 발생시킵니다.
     * 이메일 인증 코드에 validate 를 호출합니다.
     * 확인이된 이메일 코드는 삭제합니다.
     * 회원 엔티티를 생성하고 영속화합니다.
     * Domain 계층에 의존성만을 가지고 있습니다. 추후 Infra 계층에 의존성을 가질 수도 있습니다.
     * @see EmailAuthenticationCode#validateCode(String)
     * @see EmailAuthenticationCodeRepository#delete(EmailAuthenticationCode)
     * @see Member#createByEmail(String, String, String, String, String, MemberPasswordEncoder)
     * @see MemberRepository#save(Member)
     */
    @Override
    @Transactional
    public Long signupByEmail(SignupForm form) {
        log.info("Member SignUp By Email");

        signupFormNullAndEmptyCheck(form);

        final String convertedEmail = MemberEmail.convertDomainToLowercase(form.getEmail());

        emailDuplicateCheck(convertedEmail);

        EmailAuthenticationCode authenticationCode = findEmailAuthenticationCode(convertedEmail);

        authenticationCode.validateCode(form.getAuthenticationCode());

        emailAuthenticationCodeRepository.delete(authenticationCode);

        Member member = Member.createByEmail(form.getEmail(), form.getPassword(), form.getNickname(), form.getContact(), profileImageUploader.getDefaultProfileImage().getImageLocation(), memberPasswordEncoder);

        return persistedMember(member).getId();
    }

    private EmailAuthenticationCode findEmailAuthenticationCode(String email) {
        return emailAuthenticationCodeRepository.findByEmail(email)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_AUTHENTICATION_EMAIL));
    }

    private void emailDuplicateCheck(String email) {
        boolean exists = memberRepository.findMembersByEmail(MemberEmail.convertDomainToLowercase(email)).stream()
                .anyMatch(Member::isActive);

        if (exists) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_EMAIL);
        }
    }

    private Member persistedMember(Member member) {
        return memberRepository.save(member);
    }

    private void signupFormNullAndEmptyCheck(SignupForm form) {

        final List<String> fieldNames = new ArrayList<>();

        if (form.getEmail() == null || form.getEmail().isBlank()) {
            fieldNames.add("Email");
        }

        if (form.getPassword() == null || form.getPassword().isBlank()) {
            fieldNames.add("Password");
        }

        if (form.getNickname() == null || form.getNickname().isBlank()) {
            fieldNames.add("Nickname");
        }

        if (form.getContact() == null || form.getContact().isBlank()) {
            fieldNames.add("Contact");
        }

        if (form.getAuthenticationCode() == null || form.getAuthenticationCode().isBlank()) {
            fieldNames.add("Authentication Code");
        }

        if (form.getIpAddress() == null || form.getIpAddress().isBlank()) {
            fieldNames.add("IpAddress");
        }

        if (!fieldNames.isEmpty()) {
            throwValidationErrorWithMessage("Null or empty fields: " + String.join(", ", fieldNames));
        }

    }

    private void throwValidationErrorWithMessage(String message) {
        throw CustomRuntimeException.createWithApiResponseStatusAndMessage(VALIDATION_ERROR, message);
    }

}
