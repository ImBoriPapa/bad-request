package com.study.badrequest.question.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.question.command.application.dto.ModifyQuestionForm;
import com.study.badrequest.question.command.application.dto.UpdateQuestionWriterForm;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.common.response.ApiResponseStatus.NOT_FOUND_QUESTION;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionModifyServiceImpl implements QuestionModifyService {
    // TODO: 2023/08/17 이벤트 발행
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 제목,본문 수정
     *
     * @param form form (ModifyQuestionForm)
     *             questionId  (Long)
     *             requesterId (Long) 회원 식별 아이디: MemberId
     *             title       (String)
     *             contents    (String)
     *             imageIds    (List<Long>) Images Inserted into Contents
     * @return questionId (Long)
     */
    @Transactional
    public Long modifyQuestion(ModifyQuestionForm form) {
        log.info("Modify Question Title and Contents");
        Question question = findQuestionById(form.getQuestionId());
        question.modifyTitleAndContents(form.getRequesterId(), form.getTitle(), form.getContents());
        return question.getId();
    }

    @Transactional
    public Long updateQuestionWriter(UpdateQuestionWriterForm form) {
        log.info("Update Question Writer");
        Question question = findQuestionById(form.getMemberId());
        question.updateWriter(form.getNickname(), form.getProfileImage(), form.getActiveScore());
        return question.getId();
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));
    }
}
