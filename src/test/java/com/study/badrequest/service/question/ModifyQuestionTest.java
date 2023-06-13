package com.study.badrequest.service.question;

import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ModifyQuestionTest extends QuestionServiceTestBase {

    @Test
    @DisplayName("test")
    void test1() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1234L;
        QuestionRequest.Modify request = new QuestionRequest.Modify();
        //when
        given(questionRepository.findById(any())).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> questionService.modifyQuestionProcessing(memberId, questionId, request))
                .isInstanceOf(CustomRuntimeException.class);
    }
}
