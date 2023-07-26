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
import static com.study.badrequest.domain.question.QuestionSortType.*;
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
        QuestionSortType sort = NEW_EAST;

        QuestionSearchCondition condition = new QuestionSearchCondition(lastOfData, size, sort);
        //when
        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);
        //then
        assertThat(result.getSize() == 10).isTrue();
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getSortBy() == NEW_EAST).isTrue();
        assertThat(result.getLastOfData()).isNotNull();
        assertThat(result.getResults().size() == 10).isTrue();
    }

    @Test
    @DisplayName("질문 리스트 조회 테스트: 조회 많은 순서정렬")
    void test2() throws Exception {
        //given
        Long lastOfData = null;
        Integer size = 3;
        QuestionSortType sort = VIEW;

        QuestionSearchCondition condition = new QuestionSearchCondition(lastOfData, size, sort);


        //when
        Question 조회수3 = questionRepository.findById(10L).get();
        조회수3.getQuestionMetrics().incrementCountOfView();
        조회수3.getQuestionMetrics().incrementCountOfView();
        조회수3.getQuestionMetrics().incrementCountOfView();

        Question 조회수2 = questionRepository.findById(9L).get();
        조회수2.getQuestionMetrics().incrementCountOfView();
        조회수2.getQuestionMetrics().incrementCountOfView();

        Question 조회수1 = questionRepository.findById(8L).get();
        조회수1.getQuestionMetrics().incrementCountOfView();

        entityManager.flush();
        entityManager.clear();

        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getSize() == 3).isTrue();
        assertThat(result.getLastOfData() == 1L).isTrue();
        assertThat(result.getSortBy() == VIEW).isTrue();
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getResults().size() == 3).isTrue();
        assertThat(result.getResults().get(0).getId()).isEqualTo(조회수3.getId());
        assertThat(result.getResults().get(1).getId()).isEqualTo(조회수2.getId());
        assertThat(result.getResults().get(2).getId()).isEqualTo(조회수1.getId());
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
