package com.study.badrequest.api;

import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.board.repository.query.BoardQueryRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.service.BoardCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryRepository boardQueryRepository;

    @PostMapping("/api/board")
    public ResponseEntity postBoard(@RequestBody BoardRequest.Create form,
                                    List<MultipartFile> images) {

        EntityModel<BoardResponse.Create> entityModel = EntityModel.of(boardCommandService.create(form, images));

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @GetMapping("/api/board")
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
