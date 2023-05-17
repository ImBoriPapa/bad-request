package com.study.badrequest.dto.question;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class QuestionRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
        @Size(message = "제목은 최소 1글자 이상이어야 합니다.", min = 1, max = 30)
        private String title;
        @NotNull(message = "질문을 필수로 입력해야 합니다.")
        @Size(message = "질문 내용은 최소 5글자 이상입니다.", min = 5)
        private String contents;
        @Size(message = "태그는 최소 1개 최대 5개를 사용해야합니다.", min = 1, max = 5)
        private List<String> tags = new ArrayList<>();
        private List<Long> imageIds = new ArrayList<>();

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Modify {
        @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
        @Size(message = "제목은 최소 1글자 이상이어야 합니다.", min = 1, max = 30)
        private String title;
        @NotNull(message = "질문을 필수로 입력해야 합니다.")
        @Size(message = "질문 내용은 최소 5글자 이상입니다.", min = 5)
        private String contents;
        private List<Long> imageIds = new ArrayList<>();

    }


}
