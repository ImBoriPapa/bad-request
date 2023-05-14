package com.study.badrequest.api.board;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.board.BoardSearchCondition;
import com.study.badrequest.exception.custom_exception.BoardExceptionBasic;
import com.study.badrequest.repository.board.query.BoardDetailDto;
import com.study.badrequest.repository.board.query.BoardListDto;
import com.study.badrequest.repository.board.BoardQueryRepository;
import com.study.badrequest.utils.modelAssembler.BoardResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.study.badrequest.commons.constants.ApiURL.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardQueryController {

    private final BoardQueryRepository boardQueryRepository;
    private final BoardResponseModelAssembler boardResponseModelAssembler;

    @GetMapping(BOARD_DETAIL_URL)
    @CustomLogTracer
    public ResponseEntity getBoard(@PathVariable("boardId") Long id,
                                   @RequestParam(name = "category", required = false) Category category) {

        Optional<BoardDetailDto> boardDetail = boardQueryRepository.findBoardDetailByIdAndCategory(id, category);

        if (boardDetail.isEmpty()) {
            throw new BoardExceptionBasic(ApiResponseStatus.NOT_FOUND_BOARD);
        }

        EntityModel<BoardDetailDto> entityModel = boardResponseModelAssembler.toModel(boardDetail.get());

        return ResponseEntity
                .ok()
                .body(new ApiResponse.Success<>(ApiResponseStatus.SUCCESS, entityModel));
    }

    @GetMapping(BOARD_LIST_URL)
    public ResponseEntity getBoardList(BoardSearchCondition condition, @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("게시판 목록 조회:");

        Long loginMemberId = null;

        if (information != null) {
            loginMemberId = information.getId();
        }

        BoardListDto boardList = boardQueryRepository.findBoardList(condition, loginMemberId);

        EntityModel<BoardListDto> entityModel = boardResponseModelAssembler.toListModel(boardList);

        return ResponseEntity
                .ok()
                .body(new ApiResponse.Success<>(ApiResponseStatus.SUCCESS, entityModel));
    }
}
