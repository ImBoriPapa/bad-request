package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.question.command.domain.QuestionMemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionMemberJpaRepository implements QuestionMemberRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Member> findById(Long id) {

        Member member = (Member) entityManager.createNativeQuery("SELECT * FROM MEMBER WHERE member_id = ? ", Member.class)
                .setParameter(1, id)
                .getSingleResult();

        return Optional.of(member);
    }
}
