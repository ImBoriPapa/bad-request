package com.study.badrequest.board.repository;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardSearchCondition;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.board.repository.query.*;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;


@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class BoardQueryRepositoryImplTest {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardQueryRepositoryImpl boardQueryRepositoryImpl;
    @Autowired
    BoardImageRepository boardImageRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("게시판 검색")
    void search() throws Exception {
        //given

        //when
        BoardSearchCondition condition1 = new BoardSearchCondition(40, 0L, null, null, null, null, null);
        BoardSearchCondition condition2 = new BoardSearchCondition(40, 0L, "Webflux", null, null, null, null);
        List<BoardListResult> results1 = boardQueryRepositoryImpl.findBoardList(condition1)
                .getResults();
        List<BoardListResult> results2 = boardQueryRepositoryImpl.findBoardList(condition2)
                .getResults();
        //then
        Assertions.assertThat(results1.size()).isEqualTo(40);
        Assertions.assertThat(results2.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("게시판 상세 검색1")
    void searchDetail1() throws Exception {
        //given
        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL)
                .orElseThrow(() -> new RuntimeException());

        Board board = Board.createBoard()
                .title("title")
                .contents("내용")
                .category(Category.KNOWLEDGE)
                .topic(Topic.JAVA)
                .member(member)
                .build();
        Board save = boardRepository.save(board);

        BoardImage boardImage1 = BoardImage.builder()
                .board(board)
                .originalFileName("originalFileName1")
                .storedFileName("storedFileName1")
                .build();
        BoardImage boardImage2 = BoardImage.builder()
                .board(board)
                .originalFileName("originalFileName2")
                .storedFileName("storedFileName2")
                .build();
        BoardImage boardImage3 = BoardImage.builder()
                .board(board)
                .originalFileName("originalFileName3")
                .storedFileName("storedFileName3")
                .build();
        boardImageRepository.saveAll(List.of(boardImage1, boardImage2, boardImage3));

        Comment comment1 = Comment.createComment()
                .text("text1")
                .board(board)
                .member(board.getMember())
                .build();
        Comment comment2 = Comment.createComment()
                .text("text2")
                .board(board)
                .member(board.getMember())
                .build();
        Comment comment3 = Comment.createComment()
                .text("text3")
                .board(board)
                .member(board.getMember())
                .build();


        commentRepository.saveAll(
                List.of(comment1, comment2, comment3)
        );

        BoardDetailDto boardDetail = boardQueryRepositoryImpl.findBoardDetail(save.getId(), null)
                .orElseThrow(() -> new IllegalArgumentException("검색 결과 없음"));
        //when



        //then

    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void commentTest() throws Exception {
        //given
        BoardDetailDto detailDto = boardQueryRepositoryImpl.findBoardDetail(2L, null)
                .orElseThrow(() -> new IllegalArgumentException(""));
        //when

        //then



    }

}