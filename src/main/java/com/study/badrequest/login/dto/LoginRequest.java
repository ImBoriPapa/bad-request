package com.study.badrequest.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Login{
        private String email;
        private String password;
    }

    public static class Logout{

    }

    private static class ReIssue{

    }
}
