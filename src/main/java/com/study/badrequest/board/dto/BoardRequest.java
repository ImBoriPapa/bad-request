package com.study.badrequest.board.dto;

import com.study.badrequest.board.entity.Category;
import com.study.badrequest.board.entity.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    public static class Create{

        private String title;
        private String context;
        private Topic topic;
        private Category category;
    }
}
