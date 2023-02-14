package com.study.badrequest.domain.comment.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

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
