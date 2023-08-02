package com.study.badrequest.domain.question;

import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;


public abstract class QuestionEntityTestBase {

    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected QuestionRepository questionRepository;
    @Autowired
    protected EntityManager em;

    @AfterEach
    void afterEach() {
        System.out.println("[데이터 베이스 Index 초기화]");
        String query1 = "ALTER TABLE member ALTER COLUMN member_id RESTART WITH 1";
        String query2 = "ALTER TABLE member_profile ALTER COLUMN member_profile_id RESTART WITH 1";
        String query3 = "ALTER TABLE question ALTER COLUMN question_id RESTART WITH 1";
        String query4 = "ALTER TABLE question_metrics ALTER COLUMN question_metrics_id RESTART WITH 1";
        em.createNativeQuery(query1).executeUpdate();
        em.createNativeQuery(query2).executeUpdate();
        em.createNativeQuery(query3).executeUpdate();
        em.createNativeQuery(query4).executeUpdate();
    }

}
