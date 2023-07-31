package com.study.badrequest.domain.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.member.Member;

import com.study.badrequest.domain.memberProfile.MemberProfile;
import com.study.badrequest.domain.memberProfile.ProfileImage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuestionTest extends QuestionEntityTestBase {

    @Test
    @DisplayName("질문 생성 테스트")
    void test1() throws Exception {
        //given
        String title = "title";
        String contents = "contents";


        Member save = memberRepository.save(createSampleMember());

        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Question question = Question.createQuestion(title, contents, save, questionMetrics);
        //when
        Question savedQuestion = questionRepository.save(question);
        Question findById = questionRepository.findById(savedQuestion.getId()).get();
        //then

        assertThat(findById.getId()).isEqualTo(savedQuestion.getId());


    }


    private Member createSampleMember() {
        Member member = Member.createWithEmail("email@email.com", "password1234!@", "01012341234");
        member.assignMemberProfile(MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));
        return member;
    }

    @Test
    @DisplayName("조회 수 증가 테스트")
    void test2() throws Exception {
        //given
        String title = "title";
        String contents = "contents";

        Member save = memberRepository.save(createSampleMember());

        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Question question = Question.createQuestion(title, contents, save, questionMetrics);
        //when
        Question saved = questionRepository.save(question);
        em.flush();
        em.clear();
        Question findById = questionRepository.findById(saved.getId()).get();
        findById.getQuestionMetrics().incrementCountOfView();
        //then
        Assertions.assertThat(findById.getQuestionMetrics().getCountOfView()).isEqualTo(1);
    }


}