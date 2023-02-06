package com.study.badrequest.utils.model;


import com.study.badrequest.api.BoardController;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BoardResponseModelAssembler implements CustomEntityModelAssemblerSupport<BoardResponse.Create, BoardListDto> {

    /**
     * Add CRUD LINK To BoardResponse.Create
     */
    public EntityModel<BoardResponse.Create> toModel(BoardResponse.Create create) {
        return EntityModel.of(create,
                linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("PUT : 게시판 수정"),
                linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("DELETE : 게시판 삭제"),
                linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("GET : 게시판 내용"),
                linkTo(BoardController.class).slash("/board").withRel("GET : 게시판 리스트")
        );
    }

    /**
     * BoardListDto -> EntityModel<BoardListDto>
     * And
     * add Link
     * And
     * add Link in List<BoardListResult>
     */
    public EntityModel<BoardListDto> toListModel(BoardListDto entity) {

        addAllLinkInBoardListResults(entity);

        Supplier<List<Link>> links = setAddAllIfSupplier(entity);

        return EntityModel.of(entity)
                .add(linkTo(BoardController.class).slash("/board").withSelfRel())
                .addAllIf(entity.getSize() > 0, links);

    }

    @Override
    public CollectionModel<BoardListDto> toCollectionModel(BoardListDto target) {
        return null;
    }

    /**
     * Setting Link in addAllIf()
     */
    private static Supplier<List<Link>> setAddAllIfSupplier(BoardListDto entity) {

        return () -> List.of(
                linkTo(methodOn(BoardController.class)
                        .getBoardList(null, null))
                        .slash("?lastIndex=" + entity.getLastIndex())
                        .withRel("GET : NEXT DATA"),

                linkTo(methodOn(BoardController.class)
                        .getBoardList(null, null))
                        .slash("?size=" + entity.getSize())
                        .withRel("GET : SEARCH BY SIZE")
        );
    }

    //ListResult 를 순환하면서 링크 추가
    private void addAllLinkInBoardListResults(BoardListDto entity) {
        entity.getResults()
                .forEach(result -> result
                        .add(linkTo(BoardController.class)
                                .slash(result.getBoardId())
                                .withRel("GET : DETAIL")));
    }


}
