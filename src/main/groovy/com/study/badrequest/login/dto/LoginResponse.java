package com.study.badrequest.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class LoginResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResult{
        private Long memberId;
        private Date accessTokenExpired;
    }

}
