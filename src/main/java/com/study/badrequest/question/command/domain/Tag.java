package com.study.badrequest.question.command.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
public final class Tag {
    private final Long id;
    private final String name;
    private final Integer countOfUsage;

    private Tag(Long id, String name, Integer countOfUsage) {
        this.id = id;
        this.name = name;
        this.countOfUsage = countOfUsage;
    }

    public static Tag createTag(String name, Integer countOfUsage) {
        return new Tag(null, name, countOfUsage);
    }

    public static Tag initialize(Long id, String name, Integer countOfUsage) {
        return new Tag(id, name, countOfUsage);
    }
}
