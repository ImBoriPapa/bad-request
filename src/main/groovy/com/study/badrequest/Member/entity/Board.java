package com.study.badrequest.Member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String context;

    private Integer like;


    private String tag;

    private Category category;

    private LocalDateTime createAt;
    private LocalDateTime updatedAt;


    public Board(String title, String context, Integer like, String tag, Category category) {
        this.title = title;
        this.context = context;
        this.like = like;
        this.tag = tag;
        this.category = category;
    }
}
