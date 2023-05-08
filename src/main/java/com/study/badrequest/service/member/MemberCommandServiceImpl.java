package com.study.badrequest.service.member;

import com.study.badrequest.domain.member.AuthenticationMailInformation;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.repository.member.AuthenticationMailInformationRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;


@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationMailInformationRepository authenticationMailInformationRepository;
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
    public MemberResponse.Create signupMemberProcessing(MemberRequestForm.SignUp form) {
        log.info("Start SignUp Member Process \n " +
                        "email    : {}\n" +
                        "nickname : {}\n" +
                        "password : PROTECTED \n" +
                        "contact  : {} \n" +
                        "authenticationCode : {}"
                , form.getEmail(), form.getNickname(), form.getContact(), form.getAuthenticationCode());

        validateForm(form);

        Member member = memberRepository.save(createMemberFromForm(form));

        eventPublisher.publishEvent(new MemberEventDto.Create(member,"회원 가입",member.getCreatedAt()));

        return new MemberResponse.Create(member);
    }

    @Override
    @Transactional
    public MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email) {
        log.info("Start issuing temporary passwords email: {}", email);

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(NOTFOUND_MEMBER));

        final String temporaryPassword = UUID.randomUUID().toString();

        member.replacePasswordToTemporaryPassword(passwordEncoder.encode(temporaryPassword));

        eventPublisher.publishEvent(new MemberEventDto.IssueTemporaryPassword(member,member.getPassword(),"임시 비밀번호 발급", LocalDateTime.now()));

        return new MemberResponse.TemporaryPassword(member);
    }

    @Override
    @Transactional
    public MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email) {
        log.info("Start send Authentication Email email: {}", email);

        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }

        AuthenticationMailInformation authenticationMailInformation = authenticationMailInformationRepository.save(new AuthenticationMailInformation(email));

        eventPublisher.publishEvent(new MemberEventDto.SendAuthenticationMail(authenticationMailInformation));

        return new MemberResponse.SendAuthenticationEmail(authenticationMailInformation);
    }

    /**
     * 연락처 변경
     */
    @Transactional
    @Override
    public MemberResponse.Update updateContactProcessing(Long memberId, String contact) {
        log.info("Update Member Contact memberId: {}, contact: {}", memberId, contact);

        ifDuplicateContactThrowException(contact);

        Member member = findMemberById(memberId);
        member.changeContact(contact);

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"연락처 변경",member.getUpdatedAt()));

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
            throw new MemberException(NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        Member member = findMemberById(memberId);

        passwordCheck(form.getCurrentPassword(), member.getPassword());

        member.changePassword(passwordEncoder.encode(form.getNewPassword()));

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"비밀번호 변경",member.getUpdatedAt()));

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

        eventPublisher.publishEvent(new MemberEventDto.Delete(member,"회원 탈퇴 요청",LocalDateTime.now()));

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

    private void validateForm(MemberRequestForm.SignUp form) {

        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new MemberException(DUPLICATE_EMAIL);
        }

        ifDuplicateContactThrowException(form.getContact());

        AuthenticationMailInformation authenticationMailInformation = authenticationMailInformationRepository
                .findById(form.getEmail())
                .orElseThrow(() -> new MemberException(NOTFOUND_AUTHENTICATION_EMAIL));

        authenticationMailInformation.checkConfirmMail(form.getAuthenticationCode());

    }

    private void ifDuplicateContactThrowException(String contact) {
        if (memberRepository.existsByContact(contact)) {
            throw new MemberException(DUPLICATE_CONTACT);
        }
    }


    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOTFOUND_MEMBER));
    }

    //비밀번호 비교
    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new MemberException(WRONG_PASSWORD);
        }
    }

}
