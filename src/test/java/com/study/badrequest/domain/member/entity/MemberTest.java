package com.study.badrequest.domain.member.entity;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Slf4j
class MemberTest extends BaseMemberTest{
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원 생성 테스트")
    void 회원생성() throws Exception {
        //given
        Member member = Member.createMember()
                .email("member@member.com")
                .nickname("nickname")
                .password("password1234")
                .contact("01011112222")
                .authority(Authority.MEMBER)
                .build();
        //when
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getNickname()).isEqualTo(savedMember.getNickname());
        assertThat(findMember.getContact()).isEqualTo(savedMember.getContact());
        assertThat(findMember.getAboutMe()).isNotEmpty();
        assertThat(findMember.getProfileImage()).isNull();
        assertThat(findMember.getCreatedAt()).isEqualTo(savedMember.getCreatedAt());
        assertThat(findMember.getUpdatedAt()).isEqualTo(savedMember.getUpdatedAt());
    }


    /**
     * 테스트 보강
     */
    @Test
    @Disabled
    public void createMember_concurrencyTest() throws InterruptedException {
        final int numThreads = 10;

        Member member1 = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();

        Member member2 = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();

        ConcurrentLinkedQueue<String> linkedQueue = new ConcurrentLinkedQueue<>();
        // 동시 요청용 쓰레드풀
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch countDownLatch = new CountDownLatch(numThreads);
        // replaceUsername() 동시 실행
        for (int i = 1; i <= numThreads; i++) {
            executorService.execute(() -> linkedQueue.add(memberRepository.save(member1).getUsername()));
            executorService.execute(() -> linkedQueue.add(memberRepository.save(member2).getUsername()));
            countDownLatch.countDown();
        }
        List<Member> members = memberRepository.findAll();
        members.forEach(member -> log.info("member id ={}, username={}", member.getId(), member.getUsername()));
        linkedQueue.forEach(member -> log.info("member username = {}", member));

        // 쓰레드 작업이 끝날때 까지 대기
        countDownLatch.await();
        executorService.shutdown();

        //then
        Assertions.assertThat(linkedQueue.size()).isEqualTo(numThreads * 2);
    }


    @Test
    public void replaceUsername_concurrencyTest() throws InterruptedException {
        //given
        final int numThreads = 10;
        ConcurrentLinkedQueue<String> store1 = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> store2 = new ConcurrentLinkedQueue<>();

        Member member1 = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();

        Member member2 = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();

        // 동시 요청용 쓰레드풀
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);


        CountDownLatch countDownLatch = new CountDownLatch(numThreads);

        // replaceUsername() 동시 실행
        for (int i = 1; i <= numThreads; i++) {

            executorService.execute(() -> {
                member1.replaceUsername();
                store1.add(member1.getUsername());
                member2.replaceUsername();
                store2.add(member2.getUsername());
                countDownLatch.countDown();
            });
        }
        // 쓰레드 작업이 끝날때 까지 대기
        countDownLatch.await();
        executorService.shutdown();

        //then
        Assertions.assertThat(store1.size()).isEqualTo(numThreads);
        Assertions.assertThat(store2.size()).isEqualTo(numThreads);
    }

    @Test
    @DisplayName("Username 데이터베이스 저장 테스트 10000")
    void usernameInitTest() throws Exception {
        //given
        int end = 10000;
        IntStream.rangeClosed(1, end)
                .forEach(i -> {
                            Member member = Member.createMember()
                                    .email(UUID.randomUUID().toString())
                                    .nickname(UUID.randomUUID().toString())
                                    .password(UUID.randomUUID().toString())
                                    .contact(UUID.randomUUID().toString())
                                    .authority(Authority.MEMBER)
                                    .build();
                            memberRepository.save(member);
                        }
                );
        //when
        long count = memberRepository.findAll().stream().count();
        //then
        assertThat(count).isEqualTo(end);
    }

    @Test
    @DisplayName("Username 으로 조회 테스트")
    void findByUsernameTest() throws Exception {
        //given
        List<Member> before = memberRepository.findAll();
        before.forEach(m -> log.info("BEFORE exists? ={} ={} ={}", m.getId(), m.getUsername(), m.getAuthority()));
        int end = 100;
        Map<String, Member> memberMap = new HashMap<>();

        IntStream.rangeClosed(1, end)
                .forEach(i -> {
                            Member member = Member.createMember()
                                    .email(UUID.randomUUID().toString())
                                    .nickname(UUID.randomUUID().toString())
                                    .password(UUID.randomUUID().toString())
                                    .contact(UUID.randomUUID().toString())
                                    .authority(Authority.MEMBER)
                                    .build();
                            Member save = memberRepository.save(member);
                            memberMap.put(save.getUsername(), save);
                        }
                );
        //when
        List<Member> all = memberRepository.findAll();

        List<Member> members = memberMap.keySet()
                .stream()
                .map(username ->
                        memberRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Username 으로 검색 불가"))
                ).collect(Collectors.toList());
        //then
        assertThat(all.size()).isEqualTo(end);
        assertThat(members.size()).isEqualTo(end);
    }

    @Test
    @DisplayName("권한 테스트 Authority ->  Collection<? extends GrantedAuthority>")
    void authorityTest() throws Exception {
        //given
        Member user = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.MEMBER)
                .build();
        Member teacher = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.TEACHER)
                .build();
        Member admin = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.ADMIN)
                .build();
        //when
        String collect1 = user.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String collect2 = teacher.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String collect3 = admin.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        //then
        assertThat(collect1).isEqualTo("ROLE_MEMBER");
        assertThat(collect2).isEqualTo("ROLE_MEMBER,ROLE_TEACHER");
        assertThat(collect3).isEqualTo("ROLE_MEMBER,ROLE_TEACHER,ROLE_ADMIN");
    }

}