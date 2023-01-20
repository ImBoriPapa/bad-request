package com.study.badrequest.board.repository.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {

    public Integer size;
    private Boolean hasNext;
    private List<BoardListResult> results = new ArrayList<>();

}
