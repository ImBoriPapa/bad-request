package com.study.badrequest.Member.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String context;
    private Integer likeCount;

    private String tag;

    private Category category;

    private LocalDateTime createAt;
    private LocalDateTime updatedAt;


    public Board(String title, String context, Integer likeCount, String tag, Category category) {
        this.title = title;
        this.context = context;
        this.likeCount = likeCount;
        this.tag = tag;
        this.category = category;
    }
}
