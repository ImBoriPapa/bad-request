package com.study.badrequest.domain.comment.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Getter
@NoArgsConstructor
public class SubCommentListDto {
    private Integer subCommentSize;
    private Boolean hasNext;
    private Long lastIndex;
    private List<SubCommentDto> results = new ArrayList<>();

    @Builder
    public SubCommentListDto(int size, boolean hasNext,long lastIndex, List<SubCommentDto> results) {
        this.subCommentSize = size;
        this.hasNext = hasNext;
        this.lastIndex = lastIndex;
        this.results = results;
    }

}
