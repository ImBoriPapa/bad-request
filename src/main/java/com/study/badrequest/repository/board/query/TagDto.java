package com.study.badrequest.repository.board.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagDto {
    private Long id;
    private String tagName;

    public TagDto(Long id, String tagName) {
        this.id = id;
        this.tagName = tagName;
    }
}
