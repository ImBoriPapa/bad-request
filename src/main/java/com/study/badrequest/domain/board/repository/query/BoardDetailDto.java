package com.study.badrequest.domain.board.repository.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    private Long boardId;
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String title;
    private String contents;
    private Integer likeCount;
    private Category category;
    private Topic topic;
    private Integer commentCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;
    private List<String> boardImages = new ArrayList<>();

    @Builder
    public BoardDetailDto(Board board, List<BoardImage> boardImages) {
        this.boardId = board.getId();
        this.memberId = board.getMember().getId();
        this.profileImage = board.getMember().getProfileImage().getFullPath();
        this.nickname = board.getMember().getNickname();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.likeCount = board.getLikeCount();
        this.category = board.getCategory();
        this.topic = board.getTopic();
        this.commentCount = board.getCommentCount();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.boardImages = boardImages.stream().map(BoardImage::getFullPath).collect(Collectors.toList());

    }
}
