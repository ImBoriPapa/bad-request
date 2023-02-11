package com.study.badrequest.domain.board.entity;

import com.study.badrequest.domain.member.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BOARD", indexes = @Index(name = "BOARD_CATEGORY_IDX", columnList = "category"))
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
    private LocalDateTime createdAt;
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<BoardImage> boardImages = new ArrayList<>();

    @Builder(builderMethodName = "createBoard")
    public Board(Member member, String title, String contents, Category category, Topic topic) {
        this.member = member;
        this.title = title;
        this.contents = contents;
        this.likeCount = 0;
        this.category = category;
        this.topic = topic;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String contents, Category category, Topic topic) {
        ifHasTitleUpdate(title);
        ifHasContents(contents);
        ifHasCategory(category);
        ifHasTopic(topic);
        this.updatedAt = LocalDateTime.now();
    }

    private void ifHasTitleUpdate(String title) {
        if (title != null) {
            this.title = title;
        }
    }

    private void ifHasContents(String contents) {
        if (contents != null) {
            this.contents = contents;
        }
    }

    private void ifHasCategory(Category category) {
        if (category != null) {
            this.category = category;
        }
    }

    private void ifHasTopic(Topic topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }

    public void increaseCommentCount() {
        ++this.commentCount;
    }

    public void decreaseCommentCount() {
        --this.commentCount;
    }

}
