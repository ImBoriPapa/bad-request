package com.study.badrequest.commons.consts;

import lombok.Getter;

@Getter
public enum CustomStatus {

    SUCCESS(1000, "요청에 성공했습니다."),
    ERROR(1001, "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR(1002, "검증에 실패했습니다."),

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
