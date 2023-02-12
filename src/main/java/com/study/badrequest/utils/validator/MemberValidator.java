package com.study.badrequest.utils.validator;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.member.dto.MemberAuthDto;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    @CustomLogTracer
    public void validateCreateForm(MemberRequest.CreateMember form) {

        validateEmail(form.getEmail());

        validateContact(form.getContact());
    }

    public void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomValidationException(CustomStatus.DUPLICATE_EMAIL);
        }
    }

    public void validateContact(String contact) {
        if (memberRepository.existsByContact(contact)) {
            throw new CustomValidationException(CustomStatus.DUPLICATE_CONTACT);
        }
    }


}
