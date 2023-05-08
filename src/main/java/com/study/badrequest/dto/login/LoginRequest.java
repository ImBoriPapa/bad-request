package com.study.badrequest.dto.login;

import lombok.AllArgsConstructor;
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
