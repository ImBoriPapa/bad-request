package com.study.badrequest.domain.question;

import com.study.badrequest.domain.activity.ActivityScore;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

        Member member = Member.createMemberWithEmail("email@email.com", "password1234!@", "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        Member save = memberRepository.save(member);

        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Question question = Question.createQuestion(title, contents, save, questionMetrics);
        //when
        Question savedQuestion = questionRepository.save(question);
        Question findById = questionRepository.findById(savedQuestion.getId()).get();
        //then
        System.out.println(member.getMemberProfile().getActivityScore());
        System.out.println(findById.getMember().getMemberProfile().getActivityScore());
        assertThat(findById.getId()).isEqualTo(savedQuestion.getId());
        assertThat(findById.getMember().getId()).isEqualTo(member.getId());
        assertThat(findById.getMember().getMemberProfile().getId()).isEqualTo(member.getMemberProfile().getId());

    }

    @Test
    @DisplayName("조회 수 증가 테스트")
    void test2() throws Exception {
        //given
        String title = "title";
        String contents = "contents";

        Member member = Member.createMemberWithEmail("email@email.com", "password1234!@", "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        Member save = memberRepository.save(member);

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