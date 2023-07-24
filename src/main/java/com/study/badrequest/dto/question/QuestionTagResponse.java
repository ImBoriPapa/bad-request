package com.study.badrequest.dto.question;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class QuestionTagResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private List<Long> questionTagIds;
    }

}
