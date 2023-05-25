package com.study.badrequest.utils.authority;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;

import java.util.Random;


public class AuthorityUtils {

    /**
     * 본인이 아니거나 관리자 권한이 없으면 회원 정보 제한
     */
    public static void verifyPermission(Long targetMemberId, Long requestMemberId, Authority requestMemberAuthority, ApiResponseStatus status) {
        if (!isRequestingMemberAuthorized(targetMemberId, requestMemberId, requestMemberAuthority)) {
            throw new CustomRuntimeException(status);
        }
    }

    private static boolean isRequestingMemberAuthorized(Long targetMemberId, Long requestMemberId, Authority authority) {

        if (!targetMemberId.equals(requestMemberId)) {
            return authority == Authority.ADMIN;
        }
        return true;
    }

}
