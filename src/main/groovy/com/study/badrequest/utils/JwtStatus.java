package com.study.badrequest.utils;

import lombok.Getter;

@Getter
public enum JwtStatus {
    ACCESS("인증 성공"),
    EXPIRED("인증 기한 만료"),
    DENIED("인증 실패"),

    EMPTY_TOKEN("토큰없음");

    private String message;

    JwtStatus(String message) {
        this.message = message;
    }
}
