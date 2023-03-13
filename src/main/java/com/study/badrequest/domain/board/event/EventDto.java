package com.study.badrequest.domain.board.event;


import com.study.badrequest.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class EventDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class BoardCreate {
        private Board createdBoard;
        private List<MultipartFile> images;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class BoardUpdate {
        private Board createdBoard;
        private List<MultipartFile> images;
    }


}
