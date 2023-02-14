package com.study.badrequest.domain.comment.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@NoArgsConstructor
public class SubCommentListDto {
    private Integer commentSize;
    private Boolean hasNext;
    private Long lastIndex;
    private List<SubCommentDto> results = new ArrayList<>();

    @Builder
    public SubCommentListDto(int size, boolean hasNext,long lastIndex, List<SubCommentDto> results) {
        this.commentSize = size;
        this.hasNext = hasNext;
        this.lastIndex = lastIndex;
        this.results = results;
    }

}
