package com.study.badrequest.domain.board.dto;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create{
        private Long memberId;
        private String title;
        private String context;
        private Topic topic;
        private Category category;
    }
}
