package com.study.badrequest.utils.authority;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.member.entity.Authority;

import java.util.Random;


public class AuthorityUtils {

    private static final Random RANDOM = new Random();
    /**
     * 본인이 아니거나 관리자 권한이 없으면 회원 정보 제한
     * Restrict access unless you or an administrator
     */
    public static void restrictAccessIfNotYouAndAdmin(Long memberId, Long requestMemberId, Authority authority) {
        if (!isRequestingMemberAuthorized(memberId, requestMemberId, authority)) {
            throw new MemberException(CustomStatus.PERMISSION_DENIED);
        }
    }
    private static boolean isRequestingMemberAuthorized(Long memberId, Long requestMemberId, Authority authority) {
        return memberId.equals(requestMemberId) || authority == Authority.ADMIN;
    }

    public static Authority randomAuthority() {
        Authority[] values = Authority.values();

        int index = RANDOM.nextInt(values.length);
        return values[index];
    }
}
