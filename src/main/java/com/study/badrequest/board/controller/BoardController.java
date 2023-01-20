package com.study.badrequest.board.controller;

import com.study.badrequest.board.entity.Category;
import com.study.badrequest.board.entity.Topic;
import com.study.badrequest.board.repository.query.BoardListDto;
import com.study.badrequest.board.repository.query.BoardQueryRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardQueryRepository boardQueryRepository;

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
