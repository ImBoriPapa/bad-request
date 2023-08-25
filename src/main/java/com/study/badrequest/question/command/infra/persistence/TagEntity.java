package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Tag;
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
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "count_of_usage")
    private Integer countOfUsage;

    protected TagEntity(Long id, String name, Integer countOfUsage) {
        this.id = id;
        this.name = name;
        this.countOfUsage = countOfUsage;
    }

    public static TagEntity fromModel(Tag tag) {
        return new TagEntity(tag.getId(), tag.getName(), tag.getCountOfUsage());
    }

    public Tag toModel() {
        return Tag.initialize(getId(), getName(), getCountOfUsage());
    }
}
