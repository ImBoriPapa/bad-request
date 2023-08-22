package com.study.badrequest.question.query.interfaces;

import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;

import com.study.badrequest.login.command.domain.CurrentMember;
import com.study.badrequest.question.query.dto.QuestionDetail;
import com.study.badrequest.question.query.dto.QuestionListResult;
import com.study.badrequest.question.query.dto.QuestionSearchCondition;

import com.study.badrequest.question.query.dao.QuestionQueryService;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.study.badrequest.common.constants.ApiURL.QUESTION_BASE_URL;
import static com.study.badrequest.common.constants.ApiURL.QUESTION_DETAIL_URL;
import static com.study.badrequest.common.response.ApiResponseStatus.SUCCESS;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryApiController {
    private final QuestionQueryService questionQueryService;
    private final QuestionModelAssembler questionModelAssembler;

    @GetMapping(value = QUESTION_BASE_URL, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestions(QuestionSearchCondition condition) {
        log.info("질문 목록 조회");
        QuestionListResult result = questionQueryService.getQuestionList(condition);

        EntityModel<QuestionListResult> entityModel = questionModelAssembler.getQuestionListModel(result, condition);

        return ResponseEntity.ok().body(ApiResponse.success(SUCCESS, entityModel));
    }

    @GetMapping("/api/v2/questions/tagged/{tagName}")
    public ResponseEntity<?> getQuestionsByTag(@PathVariable String tagName) {

        QuestionListResult result = questionQueryService.getQuestionListBy(null);

        return ResponseEntity.ok().body(ApiResponse.success(SUCCESS, result));
    }

    @GetMapping(value = QUESTION_DETAIL_URL,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestionDetail(@PathVariable Long questionId,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               @LoggedInMember CurrentMember.Information information) {

        QuestionDetail questionDetail = questionQueryService.getQuestionDetail(request, response, questionId, information);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, questionModelAssembler.createDetailModel(questionDetail)));
    }
}
