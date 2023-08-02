package com.study.badrequest.repository.question.query;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.commons.status.ExposureStatus;

import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionSortType;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.query.QuestionDetail;
import com.study.badrequest.question.query.QuestionListResult;
import com.study.badrequest.question.query.QuestionQueryRepositoryImpl;
import com.study.badrequest.question.query.QuestionSearchCondition;
import com.study.badrequest.testHelper.TestConfig;
import com.study.badrequest.testHelper.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, QuestionQueryRepositoryImpl.class, TestData.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class QuestionQueryRepositoryImplTest {
    @Autowired
    private QuestionQueryRepositoryImpl questionQueryRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private TestData testData;



    @BeforeEach
    void beforeEach() {
        log.info("INIT TEST DATA");
        testData.createSampleQuestion();
        log.info("INIT TEST DATA FINISH");
    }

    @AfterEach
    void afterEach() {
        testData.restartAutoIncrement();
    }

    @Test
    @DisplayName("질문 상세 조회- Question 노출 상태: PUBLIC, 조회 노출 상태: PUBLIC")
    void 질문상세조회1() throws Exception {
        //given
        Long questionId = 5L;
        Long loggedInMemberId = null;
        ExposureStatus status = ExposureStatus.PUBLIC;
        //when
        log.info("QUERY START");
        boolean existDetail = questionQueryRepository.findQuestionDetail(questionId, loggedInMemberId, status).isPresent();
        log.info("QUERY FINISH");
        //then
        assertThat(existDetail).isTrue();
    }

    @Test
    @DisplayName("질문 상세 조회- Question 노출 상태: DELETE, 조회 노출 상태: PUBLIC")
    void 질문상세조회2() throws Exception {
        //given
        Long questionId = 4L;
        Long loggedInMemberId = null;
        ExposureStatus status = ExposureStatus.PUBLIC;
        //when
        log.info("QUERY START");
        boolean emptyDetail = questionQueryRepository.findQuestionDetail(questionId, loggedInMemberId, status).isEmpty();
        log.info("QUERY FINISH");
        //then
        assertThat(emptyDetail).isTrue();
    }

    @Test
    @DisplayName("질문 상세 조회- 로그인한 회원 아이디와 질문자와 같을 경우")
    void 질문상세조회3() throws Exception {
        //given
        Long questionId = null;
        Long loggedInMemberId = null;
        ExposureStatus status = ExposureStatus.PUBLIC;

        Question question = questionRepository.findById(10L).get();
        questionId = question.getId();
        loggedInMemberId = question.getMember().getId();
        //when
        log.info("QUERY START");
        QuestionDetail questionDetail = questionQueryRepository.findQuestionDetail(questionId, loggedInMemberId, status).get();
        log.info("QUERY FINISH");
        //then
        assertThat(questionDetail.getIsQuestioner()).isTrue();
    }

    @Test
    @DisplayName("질문 리스트 조회 최신순")
    void 질문리스트조회1() throws Exception{
        //given
        QuestionSearchCondition condition = new QuestionSearchCondition();
        //when
        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);
        //then
        assertThat(result.getSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("질문 리스트 조회- 조회수 순")
    void 질문리스트조회2() throws Exception{
        //given
        QuestionSearchCondition condition = new QuestionSearchCondition();
        condition.setSort(QuestionSortType.VIEW);
        //when
        Question question1 = questionRepository.findById(5L).get();
        question1.getQuestionMetrics().incrementCountOfView();
        question1.getQuestionMetrics().incrementCountOfView();
        question1.getQuestionMetrics().incrementCountOfView();

        Question question2 = questionRepository.findById(10L).get();
        question2.getQuestionMetrics().incrementCountOfView();
        question2.getQuestionMetrics().incrementCountOfView();

        Question question3 = questionRepository.findById(1L).get();
        question3.getQuestionMetrics().incrementCountOfView();

        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);
        //then
        assertThat(result.getResults().get(0).getId()).isEqualTo(5L);
        assertThat(result.getResults().get(1).getId()).isEqualTo(10L);
        assertThat(result.getResults().get(2).getId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("질문 리스트 조회- 추천순")
    void 질문리스트조회3() throws Exception{
        //given
        QuestionSearchCondition condition = new QuestionSearchCondition();
        condition.setSort(QuestionSortType.RECOMMEND);
        //when
        Question question1 = questionRepository.findById(10L).get();
        question1.getQuestionMetrics().incrementCountOfRecommendations();
        question1.getQuestionMetrics().incrementCountOfRecommendations();
        question1.getQuestionMetrics().incrementCountOfRecommendations();

        Question question2 = questionRepository.findById(15L).get();
        question2.getQuestionMetrics().incrementCountOfRecommendations();
        question2.getQuestionMetrics().incrementCountOfRecommendations();

        Question question3 = questionRepository.findById(5L).get();
        question3.getQuestionMetrics().incrementCountOfRecommendations();

        QuestionListResult result = questionQueryRepository.findQuestionListByCondition(condition);
        //then
        assertThat(result.getResults().get(0).getId()).isEqualTo(10L);
        assertThat(result.getResults().get(1).getId()).isEqualTo(15L);
        assertThat(result.getResults().get(2).getId()).isEqualTo(5L);

    }
}