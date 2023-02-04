package com.study.badrequest.domain.comment.entity;

import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.board.entity.Board;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;
    private String text;
    @OneToMany(mappedBy = "comment")
    private List<SubComment> subCommentList = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Builder
    public Comment(String text, Member member, Board board) {
        this.text = text;
        this.member = member;
        this.board = board;
    }

    public void modify(String text) {
        if (StringUtils.hasText(text)) {
            this.text = text;
        }
    }
}
