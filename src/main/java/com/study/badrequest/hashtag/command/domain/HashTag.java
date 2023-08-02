package com.study.badrequest.hashtag.command.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "hashTagName"})
@Table(name = "hash_tag", indexes = {
        @Index(name = "HASH_TAG_NAME_IDX", columnList = "hash_tag_name")
})
@Getter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    private Long id;
    @Column(name = "hash_tag_name")
    private String hashTagName;
    @Column(name = "count_of_usage")
    private Integer countOfUsage;

    protected HashTag(String hashTagName, Integer countOfUsage) {
        this.hashTagName = hashTagName;
        this.countOfUsage = countOfUsage;
    }

    public static HashTag createHashTag(String hashTagName) {
        return new HashTag(hashTagName, 0);
    }

    public void incrementUsage() {
        ++this.countOfUsage;
    }
}
