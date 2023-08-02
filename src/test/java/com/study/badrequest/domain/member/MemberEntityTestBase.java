package com.study.badrequest.domain.member;

import com.study.badrequest.member.command.domain.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public abstract class MemberEntityTestBase {

    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected EntityManager em;

    @AfterEach
    void afterEach() {
        log.info("-After Each-");
        final String query = "ALTER TABLE member ALTER COLUMN member_id RESTART WITH 1";
        em.createNativeQuery(query).executeUpdate();
    }

}
