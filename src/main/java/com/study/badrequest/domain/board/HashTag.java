package com.study.badrequest.domain.board;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;



@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "hashTagName"})
@Getter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HASHTAG_ID")
    private Long id;
    @Column(name = "TAG_NAME")
    private String hashTagName;

    public HashTag(String hashTagName) {
        this.hashTagName = hashTagName;
    }
}
