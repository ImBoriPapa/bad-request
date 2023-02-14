package com.study.badrequest.domain.comment.entity;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.board.entity.Board;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    private Integer likeCount;
    private Integer subCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "createComment")
    public Comment(String text, Member member, Board board) {
        this.text = text;
        this.member = member;
        this.board = board;
        this.likeCount = 0;
        this.subCommentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void modify(String text) {
        if (StringUtils.hasText(text)) {
            this.text = text;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void increaseSubCount() {
        ++this.subCommentCount;
    }

    public void decreaseSubCount() {

        --this.subCommentCount;
    }
}
