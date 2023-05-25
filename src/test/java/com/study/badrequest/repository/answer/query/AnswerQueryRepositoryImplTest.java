package com.study.badrequest.repository.answer.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.domain.answer.Answer;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.testHelper.TestConfig;
import com.study.badrequest.testHelper.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, AnswerQueryRepositoryImpl.class, TestData.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class AnswerQueryRepositoryImplTest {

    @Autowired
    private AnswerQueryRepositoryImpl answerQueryRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private TestData testData;

    @Test
    @DisplayName("테스트")
    void 테스트() throws Exception {
        //given
        testData.createSampleAnswer();
        //when
        AnswerResult answerResult = answerQueryRepository.findAnswerByQuestionId(10L,null,null,5L);
        //then

    }

}