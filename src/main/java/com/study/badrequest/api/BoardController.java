package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.board.repository.query.BoardQueryRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.service.BoardCommandService;
import com.study.badrequest.exception.custom_exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryRepository boardQueryRepository;

    @PostMapping("/board")
    @CustomLogger
    public ResponseEntity postBoard(@Valid
                                        @RequestPart(value = "form", required = true) BoardRequest.Create form,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                    BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR,bindingResult);
        }

        BoardResponse.Create create = boardCommandService.create(form, images);

        EntityModel<BoardResponse.Create> model = EntityModel.of(create);
        model.add(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("PUT : 게시판 수정"));
        model.add(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("DELETE : 게시판 삭제"));
        model.add(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).withRel("GET : 게시판 내용"));
        model.add(linkTo(BoardController.class).slash("/board").withRel("GET : 게시판 리스트"));

        return ResponseEntity
                .created(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).toUri())
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    @GetMapping("/board")
    @CustomLogger
    public ResponseEntity getBoardList(@RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "lastIndex", defaultValue = "0") Long lastIndex,
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam("category") Category category,
                                       @RequestParam("topic") Topic topic) {
        log.info("[getBoardList]");
        log.info("[getBoardList.size= {}]", size);
        log.info("[getBoardList.lastIndex= {}]", lastIndex);
        log.info("[getBoardList.keyword= {}]", keyword);
        log.info("[getBoardList.category= {}]", category);
        log.info("[getBoardList.topic= {}]", topic);
        BoardListDto boardList = boardQueryRepository.findBoardList(size, lastIndex, keyword, category, topic);

        return ResponseEntity.ok().body(new ResponseForm.Of<>(CustomStatus.SUCCESS, boardList));
    }

}
