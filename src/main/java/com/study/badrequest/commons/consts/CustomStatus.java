package com.study.badrequest.commons.consts;

import lombok.Getter;

@Getter
public enum CustomStatus {
    SUCCESS(1000, "요청에 성공했습니다."),
    LOGOUT_SUCCESS(1001, "로그아웃 요청이 성공했습니다."),
    ERROR(1002, "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR(1003, "요청 값 검증에 실패했습니다."),
    PERMISSION_DENIED(1004, "접근 권한이 없습니다."),
    //JWT 토큰 관련
    TOKEN_NOT_MATCH(1500, "저장된 토큰과 일치하지 않습니다."),
    TOKEN_IS_EMPTY(1501, "AccessToken 이 없습니다."),
    TOKEN_IS_EXPIRED(1502, "토큰의 유효기간이 만료되었습니다."),
    TOKEN_IS_DENIED(1503, "잘못된 토큰입니다."),
    TOKEN_IS_ERROR(1504, "문제가 있는 토큰입니다."),
    REFRESH_COOKIE_IS_EMPTY(1505, "리프레시 토큰 쿠키를 찾을 수 없습니다."),
    LOGIN_FAIL(1600, "로그인에 실패했습니다."),
    ALREADY_LOGOUT(1601, "로그아웃된 계정입니다. 다시 로그인 해주세요."),
    WRONG_FILE_ERROR(2002, "잘못된 파일 형식입니다."),
    UPLOAD_FAIL_ERROR(2003, "파일 업로드에 실패했습니다."),
    NOT_SUPPORT_ERROR(2004, "지원하지 않는 파일 형식입니다."),
    DUPLICATE_EMAIL(2005, "이미 사용중인 이메일입니다."),
    DUPLICATE_CONTACT(2006, "이미 사용중인 연락처 입니다."),
    WRONG_PARAMETER(2007, "파라미터값을 확인해 주세요."),
    NOT_EXIST_CATEGORY(2008, "존재하지 않는 카테고리 입니다."),
    NOT_EXIST_TOPIC(2009, "존재하지 않는 카테고리 입니다."),
    /**
     * 회원 관련
     * code: 2000~
     */
    NOTFOUND_MEMBER(2000, "회원정보를 찾을 수 없습니다."),
    NOTFOUND_AUTHORITY(2001, "권한정보를 찾을 수 없습니다."),
    WRONG_AUTHORITY(2002, "잘못된 권한정보 입니다."),
    WRONG_PASSWORD(2003, "잘못된 비밀번호입니다."),
    WRONG_EMAIL_PATTERN(2004, "잘못된 이메일 형식입니다."),
    /**
     * 게시판 관련
     * code: 3000~
     */
    NOT_FOUND_BOARD(3000, "존재하지 않는 게시판 입니다."),
    NOT_MATCH_BOARD_WRITER(3001, "게시판 수정은 작성자만 가능합니다."),
    /**
     * 댓글 관련
     * code: 4000~
     */
    NOT_FOUND_COMMENT(4000, "존재하지 않는 댓글 입니다."),
    NOT_FOUND_SUB_COMMENT(4001, "존재하지 않는 대댓글 입니다.");

    private int code;
    private String message;

    CustomStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
