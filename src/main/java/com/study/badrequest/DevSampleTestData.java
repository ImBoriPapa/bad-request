package com.study.badrequest;

import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.entity.SubComment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.repository.SubCommentRepository;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.study.badrequest.SampleUserData.*;

@Component
@RequiredArgsConstructor
@Transactional
@Profile({"dev", "prod"})
@Slf4j
public class DevSampleTestData {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostConstruct
    void initData() {
        sampleUserData();
    }

    public void sampleUserData() {

        ProfileImage profileImage = ProfileImage.builder()
                .fullPath("https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/default/profile.jpg")
                .build();

        Member member = Member.createMember()
                .email(SAMPLE_USER_EMAIL)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .nickname(SAMPLE_USER_NICKNAME)
                .contact(SAMPLE_USER_CONTACT)
                .profileImage(profileImage)
                .authority(Authority.MEMBER)
                .build();

        Member teacher = Member.createMember()
                .email(SAMPLE_TEACHER_EMAIL)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .nickname(SAMPLE_TEACHER_NICKNAME)
                .contact(SAMPLE_TEACHER_CONTACT)
                .profileImage(profileImage)
                .authority(Authority.TEACHER)
                .build();

        Member admin = Member.createMember()
                .email(SAMPLE_ADMIN_EMAIL)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .nickname(SAMPLE_ADMIN_NICKNAME)
                .contact(SAMPLE_ADMIN_CONTACT)
                .profileImage(profileImage)
                .authority(Authority.ADMIN)
                .build();
        memberRepository.saveAll(List.of(member, teacher, admin));
    }

}
