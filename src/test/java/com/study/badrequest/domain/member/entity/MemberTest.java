package com.study.badrequest.domain.member.entity;

import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Transactional
class MemberTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE MEMBER ALTER COLUMN MEMBER_ID RESTART WITH 1")
                .executeUpdate();
    }

    @Test
    @DisplayName("회원 생성 테스트")
    void createMemberTest() throws Exception {
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
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
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
    @Transactional
    public void createMember_concurrencyTest() throws InterruptedException {
        final int numThreads = 10;

        Set<String> set = new HashSet<String>();

        // 동시 요청용 쓰레드풀
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch countDownLatch = new CountDownLatch(numThreads);
        // replaceUsername() 동시 실행
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> set.add(initMember().getUsername()));
            countDownLatch.countDown();
        }

        List<Member> all = memberRepository.findAll();
        set.addAll(all.stream().map(Member::getUsername).collect(Collectors.toList()));

        // 쓰레드 작업이 끝날때 까지 대기
        countDownLatch.await();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        //then
        Assertions.assertThat(set.size()).isEqualTo(numThreads);
    }

    private Member initMember() {
        Member member = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();
        return memberRepository.save(member);
    }

    @Test
    public void replaceUsername_concurrencyTest() throws InterruptedException {
        //given
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

        // 동시 요청용 쓰레드풀
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        final Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        // replaceUsername() 동시 실행
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                member1.replaceUsername();
                set.add(member1.getUsername());
                log.info("Member1 ID= {}, Username ={}", member1.getId(), member1.getUsername());

            });
            executorService.execute(() -> {
                member2.replaceUsername();
                set.add(member2.getUsername());
                log.info("Member2 ID= {} Username ={}", member2, member2.getUsername());
            });
        }
        // 쓰레드 작업이 끝날때 까지 대기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        //then
        Assertions.assertThat(set.size()).isEqualTo(numThreads * 2);
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
    @Transactional
    void findByUsernameTest() throws Exception {
        //given
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