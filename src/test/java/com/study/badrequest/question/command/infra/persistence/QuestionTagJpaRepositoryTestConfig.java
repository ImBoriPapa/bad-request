package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.testHelper.DatabaseCleaner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestConfiguration
public class QuestionTagJpaRepositoryTestConfig {

    @PersistenceContext
    public EntityManager entityManager;

    @Bean
    public DatabaseCleaner databaseCleaner(EntityManager entityManager) {
        return new DatabaseCleaner(entityManager);
    }


}
