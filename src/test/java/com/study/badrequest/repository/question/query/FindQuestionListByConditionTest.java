package com.study.badrequest.repository.question.query;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.QMember;
import com.study.badrequest.domain.memberProfile.QMemberProfile;
import com.study.badrequest.domain.question.QQuestionMetrics;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionSort;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.study.badrequest.domain.member.QMember.*;
import static com.study.badrequest.domain.memberProfile.QMemberProfile.*;
import static com.study.badrequest.domain.question.QQuestion.*;
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
        long lastOfData = 10L;
        int size = 20;
        ExposureStatus exposureStatus = ExposureStatus.PUBLIC;
        BooleanExpression cursorWithId = question.id.lt(lastOfData);

        //when

        //then

    }

    @Test
    @DisplayName("질문 리스트 조회 테스트: 조회 많은 순서정렬")
    void test2() throws Exception {
        //given
        long lastOfData = 10L;
        int size = 20;
        ExposureStatus exposureStatus = ExposureStatus.PUBLIC;
        BooleanExpression cursorWithId = questionMetrics.countOfView.lt(lastOfData);

        //when


        //then

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
