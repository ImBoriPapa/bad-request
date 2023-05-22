package com.study.badrequest.repository.question.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TagDto {
    private Long questionTagId;
    private Long hashTagId;
    private String hashTagName;
}
