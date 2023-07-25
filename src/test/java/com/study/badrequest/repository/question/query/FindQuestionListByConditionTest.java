package com.study.badrequest.repository.question.query;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionSortType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.study.badrequest.domain.question.QQuestionMetrics.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class FindQuestionListByConditionTest extends QuestionQueryTestBase {

    @BeforeEach
    void beforeEach() {
        createSampleQuestion(100);
    }

    @AfterEach
    void afterEach() {
        restartAutoIncrement();
    }


    // TODO: 2023/07/24 test 수정

    @Test
    @DisplayName("질문 리스트 조회 테스트: 최신 순서정렬 - OrderById")
    void test1() throws Exception {
        //given
        Long lastOfData = null;
        Integer size = null;
        QuestionSortType sort = QuestionSortType.NEW_EAST;

        QuestionSearchCondition condition = new QuestionSearchCondition(lastOfData, size, sort);
        //when
        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);
        //then
        assertThat(result.getSize() == 10).isTrue();
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getSortBy() == QuestionSortType.NEW_EAST).isTrue();
        assertThat(result.getLastOfData()).isNotNull();
        assertThat(result.getResults().size() == 10).isTrue();
    }

    @Test
    @DisplayName("질문 리스트 조회 테스트: 조회 많은 순서정렬")
    void test2() throws Exception {
        //given
        Long lastOfData = null;
        Integer size = 3;
        QuestionSortType sort = QuestionSortType.VIEW;

        QuestionSearchCondition condition = new QuestionSearchCondition(lastOfData, size, sort);

        Question question1 = questionRepository.findById(10L).get();
        question1.getQuestionMetrics().incrementCountOfView();
        question1.getQuestionMetrics().incrementCountOfView();
        question1.getQuestionMetrics().incrementCountOfView();

        Question question2 = questionRepository.findById(9L).get();
        question2.getQuestionMetrics().incrementCountOfView();
        question2.getQuestionMetrics().incrementCountOfView();

        Question question3 = questionRepository.findById(8L).get();
        question3.getQuestionMetrics().incrementCountOfView();

        entityManager.flush();
        entityManager.clear();

        //when
        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getResults().size() == 3).isTrue();
        List<QuestionDto> questionDtos = result.getResults();
        for (QuestionDto questionDto : questionDtos) {
            log.info("id: {}",questionDto.getId());
        }
        List<Question> all = questionRepository.findAll();
        for (Question question : all) {
            log.info("ID: {}, VIEW: {}",question.getId(),question.getQuestionMetrics().getCountOfView());
        }
    }

    @Test
    @DisplayName("질문 리스트 조회 테스트: 추천 많은 순서정렬")
    void test3() throws Exception {
        //given
        long lastOfData = 10L;
        int size = 20;
        ExposureStatus exposureStatus = ExposureStatus.PUBLIC;
        BooleanExpression cursorWithId = questionMetrics.countOfRecommend.lt(lastOfData);

        //when

        //then

    }

}
