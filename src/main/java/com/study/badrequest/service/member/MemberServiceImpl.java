package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;

import com.study.badrequest.repository.member.EmailAuthenticationCodeRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;


@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;
    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 회원 가입
     * 회원 가입시 기본 프로필 이미지 경로 저장
     * 비밀번호 암호화
     * Authority == MEMBER
     */
    @Override
    @Transactional
    public MemberResponse.Create signupMember(MemberRequestForm.SignUp form, String ipAddress) {
        log.info("Start SignUp Member Process \n " +
                "email    : {}\n" +
                "nickname : {}\n" +
                "password : PROTECTED \n" +
                "contact  : {} \n" +
                "authenticationCode : {}", form.getEmail(), form.getNickname(), form.getContact(), form.getAuthenticationCode());

        emailDuplicationVerification(form.getEmail());

        contactDuplicationVerification(form.getContact());

        temporaryEmailAuthenticationCodeVerification(form);

        Member member = memberRepository.save(createMemberFromForm(form));

        eventPublisher.publishEvent(new MemberEventDto.Create(member, "이메일 회원 가입", member.getCreatedAt(), ipAddress));

        return new MemberResponse.Create(member);
    }

    private void temporaryEmailAuthenticationCodeVerification(MemberRequestForm.SignUp form) {
        EmailAuthenticationCode authenticationCode = findAuthenticationCodeByEmail(form.getEmail(), NOTFOUND_AUTHENTICATION_EMAIL);

        if (!authenticationCode.getCode().equals(form.getAuthenticationCode())) {
            throw new CustomRuntimeException(WRONG_EMAIL_AUTHENTICATION_CODE);
        }

        if (LocalDateTime.now().isAfter(authenticationCode.getExpiredAt())) {
            throw new CustomRuntimeException(NOTFOUND_AUTHENTICATION_EMAIL);
        }

        emailAuthenticationCodeRepository.deleteById(authenticationCode.getId());
    }

    private EmailAuthenticationCode findAuthenticationCodeByEmail(String email, ApiResponseStatus status) {
        return emailAuthenticationCodeRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(status));
    }

    private void emailDuplicationVerification(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomRuntimeException(DUPLICATE_EMAIL);
        }
    }

    @Override
    @Transactional
    public MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email) {
        log.info("Start issuing temporary passwords email: {}", email);

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));

        final String temporaryPassword = UUID.randomUUID().toString();

        member.replacePasswordToTemporaryPassword(passwordEncoder.encode(temporaryPassword));

        eventPublisher.publishEvent(new MemberEventDto.IssueTemporaryPassword(member, member.getPassword(), "임시 비밀번호 발급", LocalDateTime.now()));

        return new MemberResponse.TemporaryPassword(member);
    }

    @Override
    @Transactional
    public MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email) {
        log.info("인증 메일 전송 시작 email: {}", email);

        if (memberRepository.existsByEmail(email)) {
            throw new CustomRuntimeException(DUPLICATE_EMAIL);
        }

        EmailAuthenticationCode authenticationCode;

        Optional<EmailAuthenticationCode> optional = emailAuthenticationCodeRepository.findByEmail(email);

        if (optional.isPresent()) {
            optional.get().replaceCode();
            authenticationCode = optional.get();
        } else {
            authenticationCode = new EmailAuthenticationCode(email);
        }
        eventPublisher.publishEvent(new MemberEventDto.SendAuthenticationMail());

        return new MemberResponse.SendAuthenticationEmail(authenticationCode.getEmail(), authenticationCode.getCreatedAt(), authenticationCode.getExpiredAt());
    }

    /**
     * 연락처 변경
     */
    @Transactional
    @Override
    public MemberResponse.Update updateContactProcessing(Long memberId, String contact) {
        log.info("Update Member Contact memberId: {}, contact: {}", memberId, contact);

        contactDuplicationVerification(contact);

        Member member = findMemberById(memberId);
        member.changeContact(contact);

        eventPublisher.publishEvent(new MemberEventDto.Update(member, "연락처 변경", member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    @Override
    public MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequestForm.ChangePassword form) {
        log.info("Start Change Password processing => memberId: {}", memberId);

        if (form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new CustomRuntimeException(NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        Member member = findMemberById(memberId);

        passwordCheck(form.getCurrentPassword(), member.getPassword());

        member.changePassword(passwordEncoder.encode(form.getNewPassword()));

        eventPublisher.publishEvent(new MemberEventDto.Update(member, "비밀번호 변경", member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 회원 탈퇴
     * memberEventPublisher 로 이벤트 발행
     */
    @Transactional
    @Override
    public MemberResponse.Delete resignMemberProcessing(Long memberId, String password) {
        log.info("Start Resign Member Process => memberId: {}", memberId);
        Member member = findMemberById(memberId);

        passwordCheck(password, member.getPassword());

        memberRepository.delete(member);

        eventPublisher.publishEvent(new MemberEventDto.Delete(member, "회원 탈퇴 요청", LocalDateTime.now()));

        return new MemberResponse.Delete();
    }


    private Member createMemberFromForm(MemberRequestForm.SignUp form) {
        return Member.createSelfRegisteredMember(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                form.getContact(),
                new MemberProfile(form.getNickname(), ProfileImage.createDefault(imageUploader.getDefaultProfileImage()))
        );
    }


    private void contactDuplicationVerification(String contact) {
        if (memberRepository.existsByContact(contact)) {
            throw new CustomRuntimeException(DUPLICATE_CONTACT);
        }
    }


    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    //비밀번호 비교
    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new CustomRuntimeException(WRONG_PASSWORD);
        }
    }

}
