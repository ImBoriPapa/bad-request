package com.study.badrequest.base;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public abstract class BaseMemberTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    /**
     * Member Entity 초기화
     */
    @AfterEach
    void afterEach() {

        log.info("============================================= Member Table Delete =============================================");
        em.createNativeQuery("DELETE FROM member")
                .executeUpdate();

        log.info("============================================= Member ID RESET =============================================");
        em.createNativeQuery("ALTER TABLE member ALTER COLUMN member_id RESTART WITH 1")
                .executeUpdate();
    }
}
