package com.study.badrequest.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardImageResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create{
        private Long id;
        private String imageLocation;
    }
}
