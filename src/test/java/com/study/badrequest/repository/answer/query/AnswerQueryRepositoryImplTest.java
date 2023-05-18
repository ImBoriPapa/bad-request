package com.study.badrequest.repository.answer.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.domain.question.Answer;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.testHelper.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class,AnswerQueryRepositoryImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class AnswerQueryRepositoryImplTest {

    @Autowired
    private AnswerQueryRepositoryImpl answerQueryRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    @DisplayName("테스트")
    void 테스트() throws Exception{
        //given
        MemberProfile memberProfile = new MemberProfile("nickname1",ProfileImage.createDefault("imageLocation"));
        Member member1 = Member.createSelfRegisteredMember("email@email.com", "password1234!@", "01012341234", memberProfile);

        Question question = Question.createQuestion()
                .title("제목입니다.")
                .contents("내용입니다.")
                .build();

        Answer answer = Answer.createAnswer()
                .member(member1)
                .contents("답변입니다.")
                .question(question)
                .build();
        //when

        //then

    }

}