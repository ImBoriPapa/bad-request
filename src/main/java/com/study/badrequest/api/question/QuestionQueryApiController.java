package com.study.badrequest.api.question;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.repository.question.query.*;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.study.badrequest.commons.constants.ApiURL.QUESTION_LIST_URL;
import static com.study.badrequest.commons.response.ApiResponseStatus.SUCCESS;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryApiController {

    private final QuestionQueryRepository questionQueryRepository;
    private final QuestionModelAssembler questionModelAssembler;

    @GetMapping(value = QUESTION_LIST_URL, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getQuestions(QuestionSearchCondition searchCondition) {
        log.info("질문 목록 조회");
        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(searchCondition);

        EntityModel<QuestionListResult> entityModel = questionModelAssembler.getQuestionListModel(result, searchCondition);

        return ResponseEntity.ok().body(new ApiResponse.Success(SUCCESS, entityModel));
    }

    @GetMapping("/api/v2/questions/tagged/{tagName}")
    public ResponseEntity getQuestionsByTag(@PathVariable String tagName) {

        QuestionListResult result = questionQueryRepository.findQuestionListByHashTag(null);

        return ResponseEntity.ok().body(new ApiResponse.Success(SUCCESS, result));
    }

    @GetMapping("/api/v2/questions/{questionId}")
    public ResponseEntity getQuestionDetail(@PathVariable Long questionId,
                                            @LoggedInMember CurrentLoggedInMember.Information information) {
        Long memberId = null;

        if (information != null) {
            memberId = information.getId();
        }

        Optional<QuestionDetail> detail = questionQueryRepository.findQuestionDetail(questionId, memberId);

        return ResponseEntity.ok()
                .body(new ApiResponse.Success<>(SUCCESS, detail));
    }
}
