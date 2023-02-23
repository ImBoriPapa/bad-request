package com.study.badrequest.base;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;


@ActiveProfiles("test")
@Transactional
@Slf4j
public abstract class BaseMemberTest {
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

    /**
     * 테스트용 Random 회원 데이터
     */
    protected static Member createRandomMember() {
        return Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .profileImage(ProfileImage.createProfileImage().fullPath("imagePath").build())
                .build();
    }

    protected List<Member> createRandomMemberList(Integer size) {
        int end = size;

        ArrayList<Member> list = new ArrayList<>();

        IntStream.rangeClosed(1, end).forEach(i -> list.add(createRandomMember()));

        return list;
    }


}
