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
    public static class ChangeIntroduce {
        String selfIntroduce;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeNickname {
        @NotEmpty(message = "닉네임을 입력해주세요")
        private String nickname;
    }
}
