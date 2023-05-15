package com.study.badrequest.event.question;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.ExposureStatus;
import com.study.badrequest.domain.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class QuestionEventDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Member member;
        private Question question;
        private List<String> tags = new ArrayList<>();
        private List<Long> images = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Modify {

        private Question question;
        private List<Long> images = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class View {

        private HttpServletRequest request;
        private HttpServletResponse response;
        private Long questionId;
        private Boolean isAnswered;
        private ExposureStatus exposureStatus;

    }
}
