package com.study.badrequest.api.question;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ResponseForm;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.repository.question.query.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.study.badrequest.commons.response.ApiResponseStatus.SUCCESS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryApiController {

    private final QuestionQueryRepository questionQueryRepository;

    @GetMapping(value = "/api/v2/questions", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getQuestions(QuestionSearchCondition questionSearchCondition) {

        QuestionDtoListResult result = questionQueryRepository.findQuestionListByCondition(questionSearchCondition);


        if (result.getHasNext()) {

            switch (result.getSortBy()) {
                case NEW_EAST:
                    questionSearchCondition.setLastIndex(result.getLastOfIndex());
                    break;
                case VIEW:
                    questionSearchCondition.setLastOfView(result.getLastOfView());
                    break;
                case RECOMMEND:
                    questionSearchCondition.setLastOfRecommend(result.getLastOfRecommend());
                    break;
            }

        }
        Link selfRel = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(questionSearchCondition)).withSelfRel();
        Link nextData = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(questionSearchCondition)).withRel("NEXT DATA");
        List<Link> links = List.of(selfRel,nextData);

        result.add(links);

        return ResponseEntity.ok().body(new ResponseForm.Of(SUCCESS, result));
    }

    @GetMapping("/api/v2/questions/keyword")
    public ResponseEntity getQuestionsByTag(QuestionSearchConditionWithHashTag condition) {

        QuestionDtoListResult result = questionQueryRepository.findQuestionListByHashTag(condition);

        return ResponseEntity.ok().body(new ResponseForm.Of(SUCCESS, result));
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
                .body(new ResponseForm.Of<>(SUCCESS, detail));
    }
}
