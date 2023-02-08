package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.query.BoardDetailDto;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.board.repository.query.BoardQueryRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.service.BoardCommandService;
import com.study.badrequest.exception.custom_exception.BoardException;
import com.study.badrequest.exception.custom_exception.RequestParamException;
import com.study.badrequest.exception.custom_exception.CustomValidationException;
import com.study.badrequest.utils.modelAssembler.BoardResponseModelAssembler;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class BoardController {
    private final BoardCommandService boardCommandService;
    private final BoardQueryRepository boardQueryRepository;
    private final BoardResponseModelAssembler boardResponseModelAssembler;

    @PostMapping("/board")
    @CustomLogTracer
    public ResponseEntity postBoard(@Valid
                                    @RequestPart(value = "form", required = true) BoardRequest.Create form,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        BoardResponse.Create create = boardCommandService.create(form, images);

        EntityModel<BoardResponse.Create> entityModel = boardResponseModelAssembler.toModel(create);

        return ResponseEntity
                .created(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).toUri())
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

    @GetMapping("/board/{boardId}")
    @CustomLogTracer
    public ResponseEntity getBoard(@PathVariable("boardId") Long id,
                                   @RequestParam(name = "category", required = false) Category category) {

        Optional<BoardDetailDto> boardDetail = boardQueryRepository.findBoardDetail(id, category);

        if (boardDetail.isEmpty()) {
            throw new BoardException(CustomStatus.NOT_FOUND_BOARD);
        }

        EntityModel<BoardDetailDto> entityModel = boardResponseModelAssembler.toModel(boardDetail.get());

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }


}
