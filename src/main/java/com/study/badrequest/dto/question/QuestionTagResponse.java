package com.study.badrequest.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class QuestionTagResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private List<Long> questionTagIds;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Add {
        private Long questionTagId;

    }

}
