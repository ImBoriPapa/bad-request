package com.study.badrequest.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create{

        private Long boardId;
        private String createAt;
    }

}
