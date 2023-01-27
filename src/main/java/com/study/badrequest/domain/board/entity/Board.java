package com.study.badrequest.domain.board.entity;

import com.study.badrequest.domain.Member.domain.entity.Member;

import com.study.badrequest.commons.LocalDateFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "CONTENTS")
    @Lob
    private String contents;
    @Column(name = "LIKE_COUNT")
    private Integer likeCount;
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY")
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(name = "TOPIC")
    private Topic topic;
    @Column(name = "COMMENT_COUNT")
    private Integer commentCount;
    @Column(name = "CREATE_AT")
    private String createdAt;
    @Column(name = "UPDATE_AT")
    private String updatedAt;

    @Builder(builderMethodName = "createBoard")
    public Board(Member member, String title, String contents, Category category, Topic topic) {
        this.member = member;
        this.title = title;
        this.contents = contents;
        this.likeCount = 0;
        this.category = category;
        this.topic = topic;
        this.commentCount = 0;
        this.createdAt = LocalDateFormatter.formatter(LocalDateTime.now());
        this.updatedAt = LocalDateFormatter.formatter(LocalDateTime.now());
    }
}