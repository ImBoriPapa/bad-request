package com.study.badrequest.question.command.domain;

import com.study.badrequest.member.command.domain.Member;

import java.util.Optional;

public interface QuestionMemberRepository {

    Optional<Member> findById(Long id);
}
