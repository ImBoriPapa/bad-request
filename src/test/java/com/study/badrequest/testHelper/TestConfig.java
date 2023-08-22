package com.study.badrequest.testHelper;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.infra.imports.MemberPasswordEncoderImpl;
import com.study.badrequest.question.command.infra.persistence.MemberInformationRepositoryImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestConfiguration
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MemberPasswordEncoder memberPasswordEncoder(PasswordEncoder passwordEncoder) {
        return new MemberPasswordEncoderImpl(passwordEncoder);
    }
    @Bean
    public MemberInformationRepositoryImpl memberInformationRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        return new MemberInformationRepositoryImpl(jpaQueryFactory);
    }

}
