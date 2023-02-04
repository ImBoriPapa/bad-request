package com.study.badrequest.domain.board.repository.query;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    private Long boardId;
    private Long memberId;
    private String nickname;
    private String title;
    private String contents;
    private Integer likeCount;
    private Category category;
    private Topic topic;
    private Integer commentCount;
    private String createdAt;
    private String updatedAt;
    private List<BoardImage> boardImages = new ArrayList<>();

    @Builder
    public BoardDetailDto(Board board, List<BoardImage> boardImages) {
        this.boardId = board.getId();
        this.memberId = board.getMember().getId();
        this.nickname = board.getMember().getNickname();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.likeCount = board.getLikeCount();
        this.category = board.getCategory();
        this.topic = board.getTopic();
        this.commentCount = board.getCommentCount();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.boardImages = boardImages;

    }
}
