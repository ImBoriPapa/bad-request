package com.study.badrequest;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev","test"})
public class SampleData {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @PostConstruct
    public void sampleAdmin(){
        log.info("[SAMPLE DATA INIT]");
        Member member = Member.createMember()
                .email("bori@gmail.com")
                .password(passwordEncoder.encode("password1234"))
                .authority(Member.Authority.USER)
                .build();
        memberRepository.save(member);
    }
}
