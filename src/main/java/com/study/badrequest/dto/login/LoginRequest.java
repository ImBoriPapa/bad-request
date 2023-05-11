package com.study.badrequest.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;



import static com.study.badrequest.commons.constants.Regex.PASSWORD;


@NoArgsConstructor
public class LoginRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Login{
        @Email(message = "이메일 형식을 확인해 주세요")
        private String email;
        @Pattern(regexp =PASSWORD,message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String password;
    }

    public static class Logout{

    }

    private static class ReIssue{

    }
}
