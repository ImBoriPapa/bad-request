package com.study.badrequest.question.command.infra.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.member.command.domain.MemberPasswordEncoder;
import com.study.badrequest.member.command.infra.password.InfrastructurePasswordEncoder;
import com.study.badrequest.testHelper.DatabaseCleaner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestConfiguration
public class MemberInformationRepositoryTestConfig {

    @PersistenceContext
    public EntityManager entityManager;

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
        return new InfrastructurePasswordEncoder(passwordEncoder);
    }

    @Bean
    public MemberInformationRepositoryImpl memberInformationRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        return new MemberInformationRepositoryImpl(jpaQueryFactory);
    }

    @Bean
    public DatabaseCleaner databaseCleaner(EntityManager entityManager){
        return new DatabaseCleaner(entityManager);
    }


}
