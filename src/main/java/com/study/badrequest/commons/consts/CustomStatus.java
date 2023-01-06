package com.study.badrequest.commons.consts;

import lombok.Getter;

@Getter
public enum CustomStatus {

    SUCCESS(1000, "요청에 성공했습니다."),
    LOGOUT_SUCCESS(1001, "로그아웃 요청이 성공했습니다."),

    ERROR(1002, "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR(1003, "검증에 실패했습니다."),
    PERMISSION_DENIED(1004, "접근 권한이 없습니다."),

    TOKEN_NOT_MATCH(1500, "저장된 토큰과 일치하지 않습니다."),
    TOKEN_IS_EMPTY(1501, "AccessToken 이 없습니다."),
    TOKEN_IS_EXPIRED(1502, "토큰의 유효기간이 만료되었습니다."),
    TOKEN_IS_DENIED(1503, "잘못된 토큰입니다."),

    REFRESH_COOKIE_IS_EMPTY(1504, "리프레시 토큰 쿠키를 찾을 수 없습니다."),
    LOGIN_FAIL(1600, "로그인에 실패했습니다."),
    ALREADY_LOGOUT(1601, "로그아웃된 계정입니다. 다시 로그인 해주세요."),
    NOTFOUND_MEMBER(2000, "회원정보를 찾을 수 없습니다."),
    WRONG_PASSWORD(2001, "잘못된 비밀번호입니다."),
    WRONG_FILE_ERROR(2002, "잘못된 파일 형식입니다."),
    UPLOAD_FAIL_ERROR(2003, "파일 업로드에 실패했습니다."),
    NOT_SUPPORT_ERROR(2004, "지원하지 않는 파일 형식입니다.");
    private int code;
    private String message;

    CustomStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
