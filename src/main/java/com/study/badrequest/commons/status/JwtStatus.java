package com.study.badrequest.commons.status;

import lombok.Getter;

@Getter
public enum JwtStatus {
    ACCESS("인증 성공"),
    EXPIRED("인증 기한 만료"),
    DENIED("인증 실패"),
    ERROR("잘못된 토큰"),
    EMPTY_TOKEN("토큰없음"),
    LOGOUT("로그아웃한 토큰");

    private final String message;

    JwtStatus(String message) {
        this.message = message;
    }
}
