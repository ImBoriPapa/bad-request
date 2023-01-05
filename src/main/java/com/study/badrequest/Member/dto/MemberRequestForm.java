package com.study.badrequest.Member.dto;


import com.study.badrequest.commons.consts.Regex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.study.badrequest.commons.consts.Regex.PASSWORD;

public class MemberRequestForm {

    @NoArgsConstructor
    @Getter
    public static class CreateMember {
        @Email(message = "형식에 맞지 않는 이메일입니다.")
        private String email;
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String password;
        @NotEmpty(message = "이름을 입력해주세요")
        private String name;
        @NotEmpty(message = "닉네임을 입력해주세요")
        private String nickname;
        @NotEmpty(message = "연락처를 입력해주세요")
        private String contact;

        @Builder
        public CreateMember(String email, String password, String name, String nickname, String contact) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.nickname = nickname;
            this.contact = contact;
        }
    }
}
