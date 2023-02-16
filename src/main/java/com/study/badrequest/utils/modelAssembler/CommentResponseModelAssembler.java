package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.board.BoardController;
import com.study.badrequest.api.board.BoardQueryController;
import com.study.badrequest.api.comment.CommentController;
import com.study.badrequest.api.comment.CommentQueryController;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import com.study.badrequest.domain.comment.repository.dto.CommentListDto;
import com.study.badrequest.domain.comment.repository.dto.SubCommentDto;
import com.study.badrequest.domain.comment.repository.dto.SubCommentListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class CommentResponseModelAssembler {
    /**
     * 댓글 삭제
     */
    public EntityModel<CommentResponse.Delete> toModel(CommentResponse.Delete target, Long boardId) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withRel("POST: 댓글 추가"));
    }

    /**
     * 댓글 수정 응답
     */
    public EntityModel<CommentResponse.Modify> toModel(CommentResponse.Modify target, Long boardId) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withRel("POST: 댓글 추가"))
                .add(linkTo(methodOn(CommentController.class).putComments(boardId, target.getCommentId(), null,null)).withRel("PUT: 댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteComments(boardId, target.getCommentId())).withRel("DELETE: 댓글 삭제"))
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, target.getCommentId(), null)).withRel("POST: 대댓글 추가"));

    }

    /**
     * 댓글 생성 응답
     */
    public EntityModel<CommentResponse.Create> toModel(CommentResponse.Create target, Long boardId) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withSelfRel())
                .add(linkTo(methodOn(CommentController.class).putComments(boardId, target.getCommentId(), null,null)).withRel("PUT: 댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteComments(boardId, target.getCommentId())).withRel("DELETE: 댓글 삭제"))
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, target.getCommentId(), null)).withRel("POST: 대댓글 추가"));
    }

    /**
     * getComments Response
     */
    public EntityModel<CommentListDto> toListModel(CommentListDto target, Long boardId) {

        addAllLinkInBoardListResults(target);

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentQueryController.class).getComments(boardId, null)).withSelfRel())
                .addAllIf(target.getCommentSize() > 0 && target.getHasNext(), setAddAllIfSupplier(target,boardId));

    }

    private Supplier<List<Link>> setAddAllIfSupplier(CommentListDto result,Long boardId) {

        return () -> List.of(
                linkTo(methodOn(CommentQueryController.class)
                        .getComments(boardId, null))
                        .slash("?lastIndex=" + result.getLastIndex())
                        .withRel("GET : NEXT DATA"),
                linkTo(methodOn(CommentQueryController.class)
                        .getComments(boardId, null))
                        .slash("?size=" + result.getCommentSize())
                        .withRel("GET : SEARCH BY SIZE")
        );
    }

    private void addAllLinkInBoardListResults(CommentListDto entity) {
        entity.getResults()
                .forEach(result -> result.add(getLinkList(result.getBoardId(), result.getCommentId())));
    }

    private static List<Link> getLinkList(Long boardId, Long commentId) {
        List<Link> links = List.of(
                linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withRel("POST : Add Comment"),
                linkTo(methodOn(CommentController.class).putComments(boardId, commentId, null,null)).withRel("PUT : Put comment"),
                linkTo(methodOn(CommentController.class).deleteComments(boardId, commentId)).withRel("DELETE : Delete comment"),
                linkTo(methodOn(CommentController.class).postSubComments(null, commentId, null)).withRel("POST : Add SubComment")
        );
        return links;
    }

    public EntityModel<SubCommentListDto> toListModel(SubCommentListDto target) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentQueryController.class).getSubComments(target.getResults().get(0).getCommentId(), null)).withSelfRel())
                .addAllIf(target.getCommentSize() > 0 && target.getHasNext(), setAddAllIfSupplier(target));
    }

    private Supplier<List<Link>> setAddAllIfSupplier(SubCommentListDto result) {

        SubCommentDto subCommentDto = result.getResults().get(0);
        Long commentId = subCommentDto.getCommentId();

        return () -> List.of(
                linkTo(methodOn(CommentQueryController.class)
                        .getSubComments(commentId, null))
                        .slash("?lastIndex=" + result.getLastIndex())
                        .withRel("GET : NEXT DATA"),
                linkTo(methodOn(CommentQueryController.class)
                        .getSubComments(commentId, null))
                        .slash("?size=" + result.getCommentSize())
                        .withRel("GET : SEARCH BY SIZE")
        );
    }

    private void addAllLinkInBoardListResults(SubCommentListDto entity) {

        SubCommentDto subCommentDto = entity.getResults().get(0);
        Long commentId = subCommentDto.getCommentId();
        Long subCommentId = subCommentDto.getSubCommentId();

        List<Link> links = List.of(
                linkTo(methodOn(CommentController.class).postSubComments(null, commentId, null)).withRel("POST : Add SubComment"),
                linkTo(methodOn(CommentController.class).putSubComments(commentId, subCommentId, null)).withRel("PUT : Put SubComment"),
                linkTo(methodOn(CommentController.class).deleteSubComments(commentId, subCommentId)).withRel("DELETE : Delete SubComment")
        );

        entity.getResults()
                .forEach(result -> result
                        .add(links));
    }
}
