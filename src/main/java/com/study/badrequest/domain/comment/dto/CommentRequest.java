package com.study.badrequest.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CommentRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Create{
        private String text;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Update{
        private String text;
    }
}
