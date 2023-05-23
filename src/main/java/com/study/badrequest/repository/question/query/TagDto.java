package com.study.badrequest.repository.question.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TagDto extends EntityModel {
    private Long id;
    private String tagName;
}
