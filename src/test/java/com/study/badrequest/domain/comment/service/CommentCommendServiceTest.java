package com.study.badrequest.domain.comment.service;

import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.Member.domain.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class CommentCommendServiceTest {

    @Autowired
    CommentCommendService commentCommendService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 작성")
    void addCommentTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("comment@comment.com")
                .nickname("commentTester")
                .build();
        Member saveMember = memberRepository.save(member);

        Board board = Board.createBoard()
                .title("title")
                .contents("contents")
                .member(member)
                .build();
        Board saveBoard = boardRepository.save(board);

        //when
        Comment comment = commentCommendService.add(board.getId(), "댓글1");
        Comment findById = commentRepository.findById(comment.getId()).orElseThrow(() -> new IllegalArgumentException(""));
        //then
        assertThat(findById.getId()).isEqualTo(comment.getId());
        assertThat(findById.getText()).isEqualTo(comment.getText());
        assertThat(findById.getBoard()).isEqualTo(saveBoard);
        assertThat(findById.getMember()).isEqualTo(saveBoard.getMember());
        assertThat(findById.getBoard().getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("before@comment.com")
                .nickname("before")
                .build();
        Member saveMember = memberRepository.save(member);

        Board board = Board.createBoard()
                .title("before")
                .contents("contents")
                .member(member)
                .build();
        Board saveBoard = boardRepository.save(board);

        commentCommendService.add(board.getId(), "before init");

        Board findBoard = boardRepository.findByTitle("before").orElseThrow(() -> new RuntimeException());
        Comment comment = commentRepository.findByBoard(findBoard).get();
        //when
        commentCommendService.delete(comment.getId());

        //then
        assertThat(findBoard.getCommentCount()).isEqualTo(0);
        assertThatThrownBy(() -> commentRepository.findById(comment.getId()).orElseThrow(() -> new IllegalArgumentException("")))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("댓글 수정")
    void modifyTest() throws Exception{
        //given
        Member member = Member.createMember()
                .email("before@comment.com")
                .nickname("before")
                .build();
        Member saveMember = memberRepository.save(member);

        Board board = Board.createBoard()
                .title("before")
                .contents("contents")
                .member(member)
                .build();
        Board saveBoard = boardRepository.save(board);

        Comment comment = commentCommendService.add(board.getId(), "before init");
        //when
        commentCommendService.modify(comment.getId(),"수정");
        Comment findById = commentRepository.findById(comment.getId()).get();
        //then
        assertThat(findById.getText()).isEqualTo("수정");
    }



}