package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class TemporaryPasswordService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

}
