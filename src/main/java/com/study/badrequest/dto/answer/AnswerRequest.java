package com.study.badrequest.dto.answer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class AnswerRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Register {
        @NotNull(message = "답변 내용을 입력해야 합니다.")
        @Size(message = "답변 내용은 최소 5글자 이상입니다.", min = 5)
        private String contents;
        private Set<Long> imageIds = new HashSet<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Modify{
        private String contents;
        private Set<Long> imageIds = new HashSet<>();
    }

}
