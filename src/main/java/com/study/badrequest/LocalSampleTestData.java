package com.study.badrequest;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;

import static com.study.badrequest.SampleUserData.*;
import static com.study.badrequest.utils.authority.AuthorityUtils.randomAuthority;

@Component
@RequiredArgsConstructor
@Transactional
@Profile("dev")
@Slf4j
public class LocalSampleTestData {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostConstruct
    void initData() {
        sampleUserData();
    }

    public void sampleUserData() {

        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/default/profile.jpg")
                .build();
        ArrayList<Member> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Member member = Member.createMember()
                    .email(SAMPLE_USER_EMAIL + i)
                    .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                    .nickname(SAMPLE_USER_NICKNAME + i)
                    .contact(SAMPLE_USER_CONTACT + i)
                    .profileImage(profileImage)
                    .authority(randomAuthority())
                    .build();
            list.add(member);
        }
        memberRepository.saveAll(list);
    }

}
