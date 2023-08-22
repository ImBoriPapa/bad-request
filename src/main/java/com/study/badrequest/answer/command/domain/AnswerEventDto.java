package com.study.badrequest.answer.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnswerEventDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Register{
        private Answer answer;
        private MemberEntity member;
    }
}
