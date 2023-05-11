package com.study.badrequest.utils.validator;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.custom_exception.BasicCustomValidationException;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import static com.study.badrequest.commons.response.ApiResponseStatus.VALIDATION_ERROR;

@Component
@Slf4j
public class RequestValidUtils {

    public  void throwMemberExceptionIfNotMatchMemberId(Long memberId, Long loggedInMemberId ) {
        log.info("Requested ID: {}, Current Id: {}",memberId,loggedInMemberId);
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberExceptionBasic(ApiResponseStatus.NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER);
        }
    }

    public  void throwValidationExceptionIfErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Member Validation Error");
            throw new BasicCustomValidationException(VALIDATION_ERROR, bindingResult);
        }
    }
}
