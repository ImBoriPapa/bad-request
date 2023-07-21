package com.study.badrequest.event.question;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.Member;

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
    public static class CreateEvent {
        private Long memberId;
        private Long questionId;
        private List<String> tags;
        private List<Long> images;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ModifyEvent {

        private Long memberId;
        private Long questionId;
        private List<Long> images = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DeleteEvent {
        private Question question;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ViewEvent {
        private HttpServletRequest request;
        private HttpServletResponse response;
        private Long questionId;

    }
}
