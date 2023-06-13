package com.study.badrequest.domain.member;

import com.study.badrequest.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MemberEntityTestBase {

    @Autowired
    protected MemberRepository memberRepository;

}
