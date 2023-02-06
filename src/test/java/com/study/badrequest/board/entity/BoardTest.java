package com.study.badrequest.board.entity;

import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.BoardRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BoardTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;


    @Test
    @DisplayName("create")
    void board() throws Exception {
        //given

        Member member = Member.createMember()
                .email("email@email.com")
                .nickname("nickname")
                .contact("010-1122-1234")
                .authority(Member.Authority.MEMBER)
                .build();
        memberRepository.save(member);
        Board board = Board.createBoard()
                .title("title")
                .contents("tototototot")
                .category(Category.KNOWLEDGE)
                .member(member)
                .build();
        boardRepository.save(board);
        //when
        boardRepository.findAll();

        //then

    }

}