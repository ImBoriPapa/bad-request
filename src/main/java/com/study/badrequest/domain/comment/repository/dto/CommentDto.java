package com.study.badrequest.domain.comment.repository.dto;


import com.study.badrequest.domain.comment.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long boardId;
    private Long memberId;

    private String profileImage;
    private String nickname;
    private String text;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SubCommentDto> subComments = new ArrayList<>();

    /**
     * findCommentByBoardUseTwoQuery 사용시 subComments 에 값을 저장하기 위함
     */
    public void addSub(SubCommentDto dto) {
        this.subComments.add(dto);
    }

    /**
     * findCommentByBoardJustOneQuery 용 생성자
     */
    public CommentDto(Comment comment) {
        this.commentId = comment.getId();
        this.boardId = comment.getBoard().getId();
        this.memberId = comment.getMember().getId();
        this.nickname = comment.getMember().getNickname();
        this.text = comment.getText();
        this.subComments = comment
                .getSubCommentList()
                .stream()
                .map(SubCommentDto::new)
                .collect(Collectors.toList());
    }


}
