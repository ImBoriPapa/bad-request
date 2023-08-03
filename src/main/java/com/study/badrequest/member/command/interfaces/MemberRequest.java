package com.study.badrequest.member.command.interfaces;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.study.badrequest.common.constants.Regex.PASSWORD;

public class MemberRequest {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SignUp {
        @Email(message = "형식에 맞지 않는 이메일입니다.")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String password;
        @NotEmpty(message = "닉네임을 입력해주세요")
        private String nickname;
        @NotEmpty(message = "연락처를 입력해주세요")
        private String contact;
        @NotEmpty(message = "인증 코드를 입력해주세요")
        private String authenticationCode;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeIntroduce{
        String selfIntroduce;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeNickname{
        @NotEmpty(message = "닉네임을 입력해주세요")
        private String nickname;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangePassword {
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String currentPassword;
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String newPassword;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateContact {
        @NotEmpty(message = "연락처를 입력해주세요")
        private String contact;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SendAuthenticationEmail{
        @Email(message = "형식에 맞지 않는 이메일입니다.")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class IssueTemporaryPassword{
        @NotEmpty(message = "이메일을 입력해주세요")
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DeleteMember {
        @NotEmpty(message = "비밀번호를 입력해 주세요")
        private String password;
    }
}
