package com.study.badrequest.base;

import com.study.badrequest.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public abstract class MemberTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void beforeEach() {
        entityManager.
    }


}
