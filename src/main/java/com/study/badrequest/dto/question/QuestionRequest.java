package com.study.badrequest.dto.question;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class QuestionRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class CreateForm{
        private String title;
        private String contents;
        private List<String> tags = new ArrayList<>();

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ModifyForm{
        private String title;
        private String contents;
        private List<String> tags = new ArrayList<>();
    }


}
