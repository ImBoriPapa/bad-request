package com.study.badrequest.event.question;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.ExposureStatus;
import com.study.badrequest.domain.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class QuestionEventDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create{
        private Member member;
        private Question question;
        private List<String> tags = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class View{
        private Long questionId;
        private Boolean isAnswered;
        private ExposureStatus exposureStatus;

    }
}
