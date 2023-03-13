package com.study.badrequest.domain.board.entity;

import com.study.badrequest.domain.member.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;


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
        modifyUpdateTime();
    }


    public void titleUpdateIfHasChange(String title) {
        // 제목 수정시 null, 공백 허용값은 수정에 반영 x
        if (StringUtils.hasText(title)) {
            this.title = title;
        }
        modifyUpdateTime();
    }

    public void contentsUpdateIfNotNull(String contents) {
        // 내용 수정은 공백 허용
        if (contents != null) {
            this.contents = contents;
        }
        modifyUpdateTime();
    }

    public void categoryUpdateIfNotNull(Category category) {
        if (category != null) {
            this.category = category;
        }
        modifyUpdateTime();
    }

    public void topicUpdateIfNotNull(Topic topic) {
        if (topic != null) {
            this.topic = topic;
        }
        modifyUpdateTime();
    }

    private void modifyUpdateTime() {
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount >= 1) {
            this.commentCount--;
        }
    }
}
