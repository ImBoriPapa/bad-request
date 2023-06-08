package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;

import com.study.badrequest.repository.member.EmailAuthenticationCodeRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.member.TemporaryPasswordRepository;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private final TemporaryPasswordRepository temporaryPasswordRepository;
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
    public MemberResponse.Create signupMemberProcessingByEmail(MemberRequestForm.SignUp form, String ipAddress) {
        log.info("SignUp Member Processing By Email \n " +
                "email    : {}\n" +
                "nickname : {}\n" +
                "password : PROTECTED \n" +
                "contact  : {} \n" +
                "authenticationCode : {} \n" +
                "requested At: {}", form.getEmail(), form.getNickname(), form.getContact(), form.getAuthenticationCode(), LocalDateTime.now());

        final String email = form.getEmail().toLowerCase();
        final String encodedPassword = passwordEncoder.encode(form.getPassword());
        final String contact = form.getContact();
        final String nickname = form.getNickname();
        final String emailAuthenticationCode = form.getAuthenticationCode();

        emailDuplicateVerification(email);

        contactDuplicationVerification(contact);

        emailAuthenticationCodeVerification(email, emailAuthenticationCode);

        Member newMember = memberRepository.save(createMemberFromForm(email, encodedPassword, contact, nickname));

        eventPublisher.publishEvent(new MemberEventDto.Create(newMember.getId(), "이메일 회원 가입", ipAddress, newMember.getCreatedAt()));

        return new MemberResponse.Create(newMember);
    }

    private void emailDuplicateVerification(String email) {
        boolean isDuplicateEmail = memberRepository.findMembersByEmail(email)
                .stream()
                .anyMatch(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN);

        if (isDuplicateEmail) {
            throw new CustomRuntimeException(DUPLICATE_EMAIL);
        }
    }

    private void contactDuplicationVerification(String contact) {
        boolean isDuplicateContact = memberRepository.findMembersByContact(contact)
                .stream()
                .anyMatch(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN);

        if (isDuplicateContact) {
            throw new CustomRuntimeException(DUPLICATE_CONTACT);
        }
    }

    private void emailAuthenticationCodeVerification(String email, String authenticationCode) {
        EmailAuthenticationCode emailAuthenticationCode = findAuthenticationCodeByEmail(email, NOTFOUND_AUTHENTICATION_EMAIL);

        if (!emailAuthenticationCode.getCode().equals(authenticationCode)) {
            throw new CustomRuntimeException(WRONG_EMAIL_AUTHENTICATION_CODE);
        }

        if (LocalDateTime.now().isAfter(emailAuthenticationCode.getExpiredAt())) {
            throw new CustomRuntimeException(NOTFOUND_AUTHENTICATION_EMAIL);
        }

        emailAuthenticationCodeRepository.delete(emailAuthenticationCode);
    }

    private EmailAuthenticationCode findAuthenticationCodeByEmail(String email, ApiResponseStatus status) {
        return emailAuthenticationCodeRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(status));
    }

    @Override
    @Transactional
    public MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String requestedEmail, String ipAddress) {
        log.info("issuing temporary passwords email: {}", requestedEmail);

        final String email = requestedEmail.toLowerCase();

        List<Member> members = memberRepository.findMembersByEmail(email);

        if (members.isEmpty()) {
            throw new CustomRuntimeException(NOTFOUND_MEMBER);
        }

        Member activeMember = getActiveMember(members);

        final String rawTemporaryPassword = generateTemporaryPassword();
        final String encodedTemporaryPassword = passwordEncoder.encode(rawTemporaryPassword);

        TemporaryPassword savedPassword = temporaryPasswordRepository.save(TemporaryPassword.createTemporaryPassword(encodedTemporaryPassword, activeMember));

        eventPublisher.publishEvent(new MemberEventDto.IssueTemporaryPassword(activeMember.getId(), rawTemporaryPassword, "임시 비밀번호 발급", ipAddress, savedPassword.getCreatedAt()));

        return new MemberResponse.TemporaryPassword(activeMember.getEmail(), savedPassword.getCreatedAt());
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Member getActiveMember(List<Member> members) {
        return members.stream()
                .filter(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN)
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String requestedEmail) {
        log.info("Send Authentication Mail requestedEmail: {}", requestedEmail);

        final String email = requestedEmail.toLowerCase();

        emailDuplicateVerification(email);

        final EmailAuthenticationCode emailAuthenticationCode;

        Optional<EmailAuthenticationCode> optionalEmailAuthenticationCode = emailAuthenticationCodeRepository.findByEmail(email);

        if (optionalEmailAuthenticationCode.isPresent()) {
            optionalEmailAuthenticationCode.get().renewAuthenticationCode();
            emailAuthenticationCode = optionalEmailAuthenticationCode.get();
        } else {
            emailAuthenticationCode = emailAuthenticationCodeRepository.save(new EmailAuthenticationCode(email));
        }

        eventPublisher.publishEvent(new MemberEventDto.SendAuthenticationMail(emailAuthenticationCode.getEmail(), emailAuthenticationCode.getCode()));

        return new MemberResponse.SendAuthenticationEmail(emailAuthenticationCode.getEmail(), emailAuthenticationCode.getCreatedAt(), emailAuthenticationCode.getExpiredAt());
    }

    @Transactional
    @Override
    public MemberResponse.Update changeContactProcessing(Long memberId, String contact, String ipAddress) {
        log.info("Update Member Contact memberId: {}, contact: {}", memberId, contact);

        contactDuplicationVerification(contact);

        Member member = findMemberById(memberId);

        if (member.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new CustomRuntimeException(NOTFOUND_MEMBER);
        }

        member.changeContact(contact);

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "연락처 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    @Override
    public MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequestForm.ChangePassword form, String ipAddress) {
        log.info("Start Change Password processing => memberId: {}", memberId);

        if (form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new CustomRuntimeException(NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        Member member = findMemberById(memberId);

        passwordCheck(form.getCurrentPassword(), member.getPassword());

        member.changePassword(passwordEncoder.encode(form.getNewPassword()));

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "비밀번호 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 회원 탈퇴
     * memberEventPublisher 로 이벤트 발행
     */
    @Transactional
    @Override
    public MemberResponse.Delete resignMemberProcessing(Long memberId, String password, String ipAddress) {
        log.info("Start Resign Member Process => memberId: {}", memberId);
        Member member = findMemberById(memberId);

        passwordCheck(password, member.getPassword());

        member.changeStatus(AccountStatus.WITHDRAWN);

        eventPublisher.publishEvent(new MemberEventDto.Delete(member.getId(), "회원 탈퇴 요청", ipAddress, LocalDateTime.now()));

        return new MemberResponse.Delete();
    }


    private Member createMemberFromForm(String email, String password, String contact, String nickname) {
        return Member.createMemberWithEmail(
                email, password, contact, new MemberProfile(nickname, ProfileImage.createDefaultImage(imageUploader.getDefaultProfileImage()))
        );
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
