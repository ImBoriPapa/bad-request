package com.study.badrequest.event.answer;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.answer.Answer;
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
