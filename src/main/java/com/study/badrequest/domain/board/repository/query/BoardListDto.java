package com.study.badrequest.domain.board.repository.query;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardListDto {

    public Integer size;
    private Boolean hasNext;
    private Long lastIndex;
    private List<BoardListResult> results = new ArrayList<>();

    public BoardListDto(Integer size, Boolean hasNext, Long lastIndex, List<BoardListResult> results) {
        this.size = size;
        this.hasNext = hasNext;
        this.lastIndex = lastIndex;
        this.results = results;
    }
}
