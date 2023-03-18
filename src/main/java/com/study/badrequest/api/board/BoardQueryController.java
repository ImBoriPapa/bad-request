package com.study.badrequest.api.board;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.BoardException;
import com.study.badrequest.commons.exception.custom_exception.RequestParamException;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.query.BoardDetailDto;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.board.repository.BoardQueryRepository;
import com.study.badrequest.utils.modelAssembler.BoardResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.study.badrequest.commons.consts.CustomURL.BASE_API_VERSION_URL;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_API_VERSION_URL)
public class BoardQueryController {

    private final BoardQueryRepository boardQueryRepository;
    private final BoardResponseModelAssembler boardResponseModelAssembler;

    @GetMapping("/board/{boardId}")
    @CustomLogTracer
    public ResponseEntity getBoard(@PathVariable("boardId") Long id,
                                   @RequestParam(name = "category", required = false) Category category) {

        Optional<BoardDetailDto> boardDetail = boardQueryRepository.findBoardDetailByIdAndCategory(id,category);

        if (boardDetail.isEmpty()) {
            throw new BoardException(CustomStatus.NOT_FOUND_BOARD);
        }

        EntityModel<BoardDetailDto> entityModel = boardResponseModelAssembler.toModel(boardDetail.get());

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @GetMapping("/board")
    @CustomLogTracer
    public ResponseEntity getBoardList(BoardSearchCondition condition, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new RequestParamException(CustomStatus.WRONG_PARAMETER, bindingResult);
        }

        BoardListDto boardList = boardQueryRepository.findBoardList(condition);

        EntityModel<BoardListDto> entityModel = boardResponseModelAssembler.toListModel(boardList);

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }
}
