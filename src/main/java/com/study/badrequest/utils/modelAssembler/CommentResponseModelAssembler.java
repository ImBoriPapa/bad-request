package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.comment.CommentController;
import com.study.badrequest.api.comment.CommentQueryController;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.dto.CommentListDto;
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
                .add(linkTo(methodOn(CommentController.class).putComments(boardId, target.getCommentId(), null, null)).withRel("PUT: 댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteComments(boardId, target.getCommentId())).withRel("DELETE: 댓글 삭제"))
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, target.getCommentId(), null)).withRel("POST: 대댓글 추가"));

    }

    /**
     * 댓글 생성 응답
     */
    public EntityModel<CommentResponse.Create> toModel(CommentResponse.Create target, Long boardId) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withSelfRel())
                .add(linkTo(methodOn(CommentController.class).putComments(boardId, target.getCommentId(), null, null)).withRel("PUT: 댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteComments(boardId, target.getCommentId())).withRel("DELETE: 댓글 삭제"))
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, target.getCommentId(), null)).withRel("POST: 대댓글 추가"));
    }

    /**
     * 댓글 조회 응답
     */
    public EntityModel<CommentListDto> toListModel(CommentListDto target, Long boardId) {

        addAllLinkInBoardListResults(target);

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentQueryController.class).getComments(boardId, null)).withSelfRel())
                .addAllIf(target.getCommentSize() > 0 && target.getHasNext(), setAddAllIfSupplier(target, boardId));

    }

    /**
     * 대댓글 생성 응답
     */
    public EntityModel<CommentResponse.CreateSub> toModel(CommentResponse.CreateSub target, Long commentId) {
        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, null, null)).withRel("POST : 대댓글 추가"))
                .add(linkTo(methodOn(CommentController.class).putSubComments(commentId, target.getSubCommentId(), null)).withRel("PUT : 대댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteSubComments(target.getSubCommentId(), null)).withRel("DELETE : 대댓글 삭제"));
    }

    /**
     * 대댓글 수정 응답
     */
    public EntityModel<CommentResponse.ModifySub> toModel(CommentResponse.ModifySub target, Long commentId) {

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, null, null)).withRel("POST : 대댓글 추가"))
                .add(linkTo(methodOn(CommentController.class).putSubComments(commentId, target.getSubCommentId(), null)).withRel("PUT : 대댓글 수정"))
                .add(linkTo(methodOn(CommentController.class).deleteSubComments(target.getSubCommentId(), null)).withRel("DELETE : 대댓글 삭제"));
    }

    /**
     * 대댓글 삭제 응답
     */
    public EntityModel<CommentResponse.DeleteSub> toModel(CommentResponse.DeleteSub target, Long commentId) {
        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentController.class).postSubComments(null, commentId, null)).withRel("POST : 대댓글 추가"));
    }

    /**
     * 다음 데이터 링크 추가
     */
    private Supplier<List<Link>> setAddAllIfSupplier(CommentListDto result, Long boardId) {

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

    /**
     * 댓글 리스트 순회하며 링크 추가
     */
    private void addAllLinkInBoardListResults(CommentListDto entity) {
        entity.getResults()
                .forEach(result -> result.add(getCommentLinkList(result.getBoardId(), result.getCommentId())));
    }

    /**
     * 댓글의 링크 생성
     */
    private static List<Link> getCommentLinkList(Long boardId, Long commentId) {
        return List.of(
                linkTo(methodOn(CommentController.class).postComments(null, boardId, null, null)).withRel("POST : Add Comment"),
                linkTo(methodOn(CommentController.class).putComments(boardId, commentId, null, null)).withRel("PUT : Put comment"),
                linkTo(methodOn(CommentController.class).deleteComments(boardId, commentId)).withRel("DELETE : Delete comment"),
                linkTo(methodOn(CommentController.class).postSubComments(null, commentId, null)).withRel("POST : Add SubComment")
        );
    }

    /**
     * 대댓글 링크 추가
     */
    public EntityModel<SubCommentListDto> toListModel(SubCommentListDto target, Long commentId) {

        addAllLinkInBoardListResults(target, commentId);

        return EntityModel.of(target)
                .add(linkTo(methodOn(CommentQueryController.class).getSubComments(commentId, null)).withSelfRel())
                .addAllIf(target.getSubCommentSize() > 0 && target.getHasNext(), setAddAllIfSupplier(target, commentId));
    }

    /**
     * 다음 대댓글 링크 추가
     */
    private Supplier<List<Link>> setAddAllIfSupplier(SubCommentListDto result, Long commentId) {

        return () -> List.of(
                linkTo(methodOn(CommentQueryController.class)
                        .getSubComments(commentId, null))
                        .slash("?lastIndex=" + result.getLastIndex())
                        .withRel("GET : NEXT DATA"),
                linkTo(methodOn(CommentQueryController.class)
                        .getSubComments(commentId, null))
                        .slash("?size=" + result.getSubCommentSize())
                        .withRel("GET : SEARCH BY SIZE")
        );
    }

    /**
     * 대댓글을 순회 하면서 링크 추가
     */
    private void addAllLinkInBoardListResults(SubCommentListDto entity, Long commentId) {

        entity.getResults()
                .forEach(result -> result.add(getSubCommentLinkList(commentId, result.getSubCommentId())));
    }

    /**
     * 대댓글에 링크 추가
     */
    private  List<Link> getSubCommentLinkList(Long commentId, Long subCommentId) {
        List<Link> links = List.of(
                linkTo(methodOn(CommentController.class).postSubComments(null, commentId, null)).withRel("POST : Add SubComment"),
                linkTo(methodOn(CommentController.class).putSubComments(commentId, subCommentId, null)).withRel("PUT : Put SubComment"),
                linkTo(methodOn(CommentController.class).deleteSubComments(commentId, subCommentId)).withRel("DELETE : Delete SubComment")
        );
        return links;
    }

}
