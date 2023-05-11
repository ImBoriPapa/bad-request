package com.study.badrequest.repository.member;


import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    Optional<Member> findByEmail(String email);

    Optional<Member> findMemberByUsernameAndAuthority(String username, Authority authority);

    Optional<Member> findMemberByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByContact(String password);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmailAndDomainName(String email, String domain);

    Optional<Member> findByIdAndAuthority(Long memberId, Authority authority);

    /**
     * 2023/ 5월 11일
     * 실행계획
     * selectType: SIMPLE
     * type: ref
     * key: MEMBER_ONE_TIME_AUTHENTICATION_CODE_IDX
     * ref: const
     * rows 1
     * extra: Using index condition
     */
    Optional<Member> findByOneTimeAuthenticationCode(String code);
}
