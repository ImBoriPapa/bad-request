package com.study.badrequest.repository.comment.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentListDto {
    private Integer commentSize;
    private Boolean hasNext;

    private Long lastIndex;
    private List<CommentDto> results = new ArrayList<>();

    @Builder
    public CommentListDto(int size, boolean hasNext,long lastIndex, List<CommentDto> results) {
        this.commentSize = size;
        this.hasNext = hasNext;
        this.lastIndex = lastIndex;
        this.results = results;
    }
}
