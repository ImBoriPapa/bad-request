package com.study.badrequest.domain.hashTag;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "hashTagName"})
@Table(name = "HASH_TAG", indexes = {
        @Index(name = "HASH_TAG_NAME_IDX", columnList = "HASH_TAG_NAME")
})
@Getter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HASHTAG_ID")
    private Long id;
    @Column(name = "HASH_TAG_NAME")
    private String hashTagName;
    @Column(name = "COUNT_OF_USAGE")
    private Integer countOfUsage;

    public HashTag(String hashTagName) {
        this.hashTagName = hashTagName;
        this.countOfUsage = 0;
    }

    public void incrementUsage() {
        ++this.countOfUsage;
    }
}
