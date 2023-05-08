package com.study.badrequest.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
public class CommentRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Create{
        @NotBlank(message = "댓글에 입력 해주세요")
        private String text;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Update{
        @NotBlank(message = "댓글에 입력 해주세요")
        private String text;
    }
}
