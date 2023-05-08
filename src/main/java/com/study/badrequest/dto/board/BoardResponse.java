package com.study.badrequest.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.board.Board;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime createAt;
    }

    @NoArgsConstructor
    @Getter
    public static class Update{
        private Long boardId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;

        public Update(Board board) {
            this.boardId = board.getId();
            this.updatedAt = board.getUpdatedAt();
        }
    }

}
