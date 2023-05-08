package com.study.badrequest.domain.board;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class BoardTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_TAG_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HASHTAG_ID")
    private HashTag hashTag;

    public static void createBoardTag(Board board,HashTag hashTag){
        BoardTag boardTag = new BoardTag();
        boardTag.addBoard(board);
        boardTag.addHashTag(hashTag);
    }

    private void addBoard(Board board) {
        if(this.board != null){
            this.board.getBoardTagList().remove(board);
        }
        this.board = board;
        board.addBoardTag(this);
    }

    private void addHashTag(HashTag tag) {
        this.hashTag = tag;
    }

}
