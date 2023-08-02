package com.study.badrequest.event.answer;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.answer.command.domain.Answer;
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
        private Member member;
    }
}
