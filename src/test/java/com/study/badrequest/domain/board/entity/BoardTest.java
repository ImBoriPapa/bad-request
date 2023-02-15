package com.study.badrequest.domain.board.entity;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.BoardRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BoardTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);

    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        boardRepository.deleteAll();
    }


    @Test
    @DisplayName("create")
    void board() throws Exception {
        //given
        String email = "tester@test.com";

        Member member = memberRepository.findByEmail(email).get();

        Board board = Board.createBoard()
                .title("title")
                .contents("내용내용내용내용내용")
                .category(Category.KNOWLEDGE)
                .topic(Topic.JAVA)
                .member(member)
                .build();
        //when
        Board saved = boardRepository.save(board);
        Board findBoard = boardRepository.findById(saved.getId()).get();
        //then
        Assertions.assertThat(findBoard.getId()).isEqualTo(saved.getId());
        Assertions.assertThat(findBoard.getTitle()).isEqualTo(saved.getTitle());
        Assertions.assertThat(findBoard.getContents()).isEqualTo(saved.getContents());
        Assertions.assertThat(findBoard.getCategory()).isEqualTo(saved.getCategory());
        Assertions.assertThat(findBoard.getTopic()).isEqualTo(saved.getTopic());
    }

}