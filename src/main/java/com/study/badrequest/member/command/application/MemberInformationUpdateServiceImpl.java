package com.study.badrequest.member.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.application.dto.ContactChangeForm;
import com.study.badrequest.member.command.application.dto.PasswordChangeForm;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.common.response.ApiResponseStatus.DUPLICATE_CONTACT;
import static com.study.badrequest.common.response.ApiResponseStatus.NOTFOUND_MEMBER;
import static com.study.badrequest.member.command.domain.AccountStatus.WITHDRAWN;

@Service
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberInformationUpdateServiceImpl implements MemberInformationUpdateService {
    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder memberPasswordEncoder;

    @Transactional
    @Override
    public Long changePassword(PasswordChangeForm form) {
        log.info("Change Password memberId: {}", form.getMemberId());

        Member member = findMemberById(form.getMemberId());
        member.changePassword(form.getCurrentPassword(), form.getNewPassword(), memberPasswordEncoder);

        return member.getId();
    }

    @Transactional
    @Override
    public Long changeContact(ContactChangeForm form) {
        log.info("Update Member Contact memberId: {}, contact: {}", form.getMemberId(), form.getContact());
        Member member = findMemberById(form.getMemberId());

        contactDuplicationVerification(form.getContact());

        member.changeContact(form.getContact());

        return member.getId();
    }

    private void contactDuplicationVerification(String contact) {
        boolean isDuplicateContact = memberRepository.findMembersByContact(contact)
                .stream()
                .anyMatch(member -> member.getAccountStatus() != WITHDRAWN);

        if (isDuplicateContact) {
            throw CustomRuntimeException.createWithApiResponseStatus(DUPLICATE_CONTACT);
        }
    }

    private Member findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        if (member.getAccountStatus() == WITHDRAWN) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER);
        }

        return member;

    }

}
