package com.study.badrequest.dto.question;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class QuestionRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class CreateForm {
        @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
        @Pattern(regexp = "\\w", message = "제목은 최소 1글자 이상이어야 합니다.")
        private String title;
        @NotBlank(message = "내요은 공백을 허용하지 않습니다.")
        @Pattern(regexp = "(\\w+\\s){4,}\\w+", message = "내용은 최소 5글자 이상입니다.")
        private String contents;
        private List<String> tags = new ArrayList<>();

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ModifyForm {
        private String title;
        private String contents;
        private List<String> tags = new ArrayList<>();
    }


}
