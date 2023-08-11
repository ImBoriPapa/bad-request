package com.study.badrequest.utils.verification;

import com.study.badrequest.common.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.common.response.ApiResponseStatus.VALIDATION_ERROR;

@Slf4j
public class RequestValidUtils {

    public static void throwMemberExceptionIfNotMatchMemberId(Long memberId, Long loggedInMemberId) {
        log.info("Requested ID: {}, Current Id: {}", memberId, loggedInMemberId);
        if (!loggedInMemberId.equals(memberId)) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER);
        }
    }

    public static void throwValidationExceptionIfErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Validation Error");
            throw CustomRuntimeException.createWithBindingResults(VALIDATION_ERROR, bindingResult);
        }
    }
}
