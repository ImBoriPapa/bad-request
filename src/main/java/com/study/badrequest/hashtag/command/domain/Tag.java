package com.study.badrequest.hashtag.command.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "name"})
@Table(name = "tag", indexes = {
        @Index(name = "TAG_NAME_IDX", columnList = "name")
})
@Getter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "count_of_usage")
    private Integer countOfUsage;

    protected Tag(String name, Integer countOfUsage) {
        this.name = name;
        this.countOfUsage = countOfUsage;
    }

    public static Tag createTag(String tag) {
        return new Tag(tag, 0);
    }

    public void incrementUsage() {
        ++this.countOfUsage;
    }
}
