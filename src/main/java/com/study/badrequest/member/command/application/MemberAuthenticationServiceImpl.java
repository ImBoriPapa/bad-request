package com.study.badrequest.member.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.mail.command.domain.MemberMail;
import com.study.badrequest.member.command.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.study.badrequest.common.response.ApiResponseStatus.DUPLICATE_EMAIL;
import static com.study.badrequest.common.response.ApiResponseStatus.NOTFOUND_MEMBER;
import static com.study.badrequest.member.command.domain.AccountStatus.WITHDRAWN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberAuthenticationServiceImpl implements MemberAuthenticationService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;

    @Transactional
    @Override
    public EmailAuthenticationCodeValidityTime issueEmailAuthenticationCode(String email) {
        log.info("Send Authentication Mail requestedEmail: {}", email);
        final String convertedEmail = MemberEmail.convertDomainToLowercase(email);
        emailDuplicateVerification(convertedEmail);

        Optional<EmailAuthenticationCode> optionalEmailAuthenticationCode = emailAuthenticationCodeRepository.findByEmail(email);

        final EmailAuthenticationCode emailAuthenticationCode;

        if (optionalEmailAuthenticationCode.isPresent()) {
            optionalEmailAuthenticationCode.get().renewAuthenticationCode();
            emailAuthenticationCode = optionalEmailAuthenticationCode.get();
        } else {
            emailAuthenticationCode = emailAuthenticationCodeRepository.save(new EmailAuthenticationCode(email));
        }

        applicationEventPublisher.publishEvent(new MemberEventDto.SendAuthenticationMail(emailAuthenticationCode.getEmail(), emailAuthenticationCode.getCode()));

        return new EmailAuthenticationCodeValidityTime();
    }

    private void emailDuplicateVerification(String email) {
        boolean isDuplicateEmail = memberRepository.findMembersByEmail(email)
                .stream()
                .anyMatch(member -> member.getAccountStatus() != WITHDRAWN);

        if (isDuplicateEmail) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_EMAIL);
        }
    }

    @Transactional
    @Override
    public Long issueTemporaryPassword(TemporaryPasswordIssuanceForm form) {
        log.info("Issuing Temporary Password email: {}", form.getEmail());

        final String email = MemberEmail.convertDomainToLowercase(form.getEmail());

        Member activeMember = findActiveMemberByEmail(email);

        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword(activeMember);

        applicationEventPublisher.publishEvent(new MemberEventDto.IssueTemporaryPassword(activeMember.getId(), temporaryPassword.getPassword(), "임시 비밀번호 발급", form.getIpAddress(), LocalDateTime.now()));

        return activeMember.getId();
    }

    private Member findActiveMemberByEmail(String email) {
        List<Member> members = memberRepository.findMembersByEmail(email);

        return getActiveMember(members);
    }

    private Member getActiveMember(List<Member> members) {
        return members.stream()
                .filter(member -> member.getAccountStatus() != WITHDRAWN)
                .findFirst()
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }
}
