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
@Profile({"dev", "test"})
public class SampleData {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public static final String SAMPLE_USER_EMAIL = "user@gmail.com";

    public static final String SAMPLE_USER_CONTACT = "010-0000-1234";
    public static final String SAMPLE_TEACHER_EMAIL = "teacher@gmail.com";
    public static final String SAMPLE_ADMIN_EMAIL = "admin@gmail.com";

    @PostConstruct
    public void sampleUser() {
        log.info("[INIT SAMPLE USER]");
        Member user = Member.createMember()
                .email(SAMPLE_USER_EMAIL)
                .password(passwordEncoder.encode("password1234!@"))
                .contact(SAMPLE_USER_CONTACT)
                .authority(Member.Authority.MEMBER)
                .build();

        Member teacher = Member.createMember()
                .email(SAMPLE_TEACHER_EMAIL)
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.TEACHER)
                .build();

        Member admin = Member.createMember()
                .email(SAMPLE_ADMIN_EMAIL)
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.ADMIN)
                .build();
        memberRepository.saveAll(List.of(user, teacher, admin));
    }
}
