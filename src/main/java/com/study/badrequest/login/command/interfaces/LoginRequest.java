package com.study.badrequest.login.command.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;



import static com.study.badrequest.common.constants.Regex.PASSWORD;


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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class LoginByOneTimeCode{
        private String code;
    }
}
