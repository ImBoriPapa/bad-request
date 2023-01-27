package com.study.badrequest.domain.comment.entity;

import com.study.badrequest.domain.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    public Comment(String text, Board board) {
        this.text = text;
        this.board = board;
    }
}
