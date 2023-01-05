package com.study.badrequest;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev","test"})
public class SampleData {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void sampleUser(){
        log.info("[INIT SAMPLE USER]");
        Member user = Member.createMember()
                .email("user@gmail.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER)
                .build();

        Member teacher = Member.createMember()
                .email("teacher@gmail.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.TEACHER)
                .build();

        Member admin = Member.createMember()
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.ADMIN)
                .build();
        memberRepository.saveAll(List.of(user, teacher, admin));
    }
}
