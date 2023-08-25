package com.study.badrequest.member.command.application;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.application.dto.MemberCreateForm;
import com.study.badrequest.member.command.domain.dto.MemberChangeContact;
import com.study.badrequest.member.command.domain.dto.MemberChangePassword;
import com.study.badrequest.member.command.domain.dto.MemberCreate;
import com.study.badrequest.member.command.domain.dto.MemberResign;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.*;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import com.study.badrequest.member.command.domain.imports.ProfileImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.values.AccountStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberManagementServiceImpl implements MemberManagementService {
    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder memberPasswordEncoder;
    private final ProfileImageUploader profileImageUploader;
    private final AuthenticationCodeGenerator authenticationCodeGenerator;

    @Override
    @Transactional
    public MemberId signupByEmail(MemberCreateForm memberCreateForm) {

        validate(memberCreateForm);

        emailDuplicateCheck(memberCreateForm.email());

        ProfileImage defaultProfileImage = profileImageUploader.getDefaultProfileImage();

        MemberProfile memberProfile = MemberProfile.createMemberProfile(null, defaultProfileImage);

        Member member = Member.createByEmail(createMemberCreate(memberCreateForm), memberProfile, authenticationCodeGenerator, memberPasswordEncoder);

        memberRepository.save(member);

        return member.getMemberId();
    }


    private MemberCreate createMemberCreate(MemberCreateForm memberCreateForm) {
        return new MemberCreate(memberCreateForm.email(), memberCreateForm.password(), memberCreateForm.contact());
    }

    @Override
    @Transactional
    public MemberId changePassword(MemberId memberId, MemberChangePassword memberChangePassword) {
        Member savedMember = getMemberByMemberId(memberId);
        Member updatedMember = savedMember.changePassword(memberChangePassword, memberPasswordEncoder);
        return memberRepository.save(updatedMember).getMemberId();
    }

    @Override
    @Transactional
    public MemberId changeContact(MemberId memberId, MemberChangeContact memberChangeContact) {
        Member savedMember = getMemberByMemberId(memberId);

        List<Member> members = memberRepository.findMembersByContact(memberChangeContact.contact());
        duplicateCheckContract(members);

        Member updatedMember = savedMember.changeContact(memberChangeContact);

        return memberRepository.save(updatedMember).getMemberId();
    }

    @Override
    @Transactional
    public LocalDateTime resign(MemberId memberId, MemberResign memberResign) {
        Member savedMember = getMemberByMemberId(memberId);
        Member resignedMember = savedMember.resign(memberResign, memberPasswordEncoder);
        return memberRepository.save(resignedMember).getResignAt();
    }

    private void duplicateCheckContract(List<Member> members) {
        boolean isDuplicateContact = members.stream().anyMatch(member -> member.getAccountStatus() == ACTIVE);

        if (isDuplicateContact) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_CONTACT);
        }
    }

    private Member getMemberByMemberId(MemberId memberId) {
        return memberRepository.findById(memberId.getId()).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }

    private void emailDuplicateCheck(String email) {

        boolean exists = memberRepository.findMembersByEmail(email).stream()
                .anyMatch(member -> member.getAccountStatus() == ACTIVE);

        if (exists) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_EMAIL);
        }
    }

    private void validate(MemberCreateForm memberCreateForm) {

        final List<String> fieldNames = new ArrayList<>();

        if (memberCreateForm.email() == null || memberCreateForm.email().isBlank()) {
            fieldNames.add("Email");
        }

        if (memberCreateForm.password() == null || memberCreateForm.password().isBlank()) {
            fieldNames.add("Password");
        }

        if (memberCreateForm.nickname() == null || memberCreateForm.nickname().isBlank()) {
            fieldNames.add("Nickname");
        }

        if (memberCreateForm.contact() == null || memberCreateForm.contact().isBlank()) {
            fieldNames.add("Contact");
        }

        if (!fieldNames.isEmpty()) {
            throwValidationErrorWithMessage("Null or empty fields: " + String.join(", ", fieldNames));
        }

    }

    private void throwValidationErrorWithMessage(String message) {
        throw CustomRuntimeException.createWithApiResponseStatusAndMessage(VALIDATION_ERROR, message);
    }

}
