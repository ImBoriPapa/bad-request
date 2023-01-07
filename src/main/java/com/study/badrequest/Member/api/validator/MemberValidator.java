package com.study.badrequest.Member.api.validator;

import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.Member.dto.MemberRequestForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.CustomValidationException;
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

    public void validateCreateForm(MemberRequestForm.CreateMember form) {
        log.info("[MemberValidator.validateCreateForm]");

        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new CustomValidationException(CustomStatus.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByContact(form.getContact())) {
            throw new CustomValidationException(CustomStatus.DUPLICATE_CONTACT);
        }
    }

    public void validateContact(MemberRequestForm.UpdateContact form) {
        log.info("[MemberValidator.validateContact]");
        if (memberRepository.existsByContact(form.getContact())) {
            throw new CustomValidationException(CustomStatus.DUPLICATE_CONTACT);
        }
    }
}
