package com.study.badrequest.board.repository;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.board.entity.Board;
import com.study.badrequest.board.entity.Category;

import com.study.badrequest.board.entity.Topic;
import com.study.badrequest.board.repository.query.BoardListResult;
import com.study.badrequest.board.repository.query.BoardQueryRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
@Transactional
class BoardQueryRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardQueryRepositoryImpl boardQueryRepositoryImpl;


    @Test
    @DisplayName("게시판 검색")
    void search() throws Exception {
        //given
        log.info("[TEST DATA INIT]");
        Member member = Member.createMember()
                .email("email@email.com")
                .nickname("nickname")
                .contact("010-1122-1234")
                .authority(Member.Authority.MEMBER)
                .build();
        memberRepository.save(member);
        List<Board> list1 = new ArrayList<>();
        List<Board> list2 = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            Board board = Board.createBoard()
                    .title("title"+i)
                    .context("tototototot")
                    .category(Category.KNOWLEDGE)
                    .topic(Topic.JAVA)
                    .member(member)
                    .build();
            list1.add(board);
        }

        for (int i = 21; i <= 40; i++) {
            Board board = Board.createBoard()
                    .title("title"+i)
                    .context("tototototot")
                    .category(Category.QUESTION)
                    .topic(Topic.MYSQL)
                    .member(member)
                    .build();
            list1.add(board);
        }
        Board board = Board.createBoard()
                .title("Webflux")
                .context("tototototot")
                .category(Category.QUESTION)
                .topic(Topic.JAVA)
                .member(member)
                .build();
        boardRepository.save(board);
        boardRepository.saveAll(list1);
        boardRepository.saveAll(list2);
        log.info("[TEST DATA INIT FINISH]");
        //when

        List<BoardListResult> results1 = boardQueryRepositoryImpl.findBoardList(40, 0L, null, null, null)
                .getResults();
        List<BoardListResult> results2 = boardQueryRepositoryImpl.findBoardList(40, 0L, "Webflux", null, null)
                .getResults();
        //then
        Assertions.assertThat(results1.size()).isEqualTo(40);
        Assertions.assertThat(results2.size()).isEqualTo(1);


    }
}