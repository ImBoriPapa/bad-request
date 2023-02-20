package com.study.badrequest.domain.comment.service;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.service.BoardCommandServiceImpl;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class CommentCommendServiceTest extends BaseMemberTest {

    @Autowired
    CommentCommendService commentCommendService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    BoardCommandServiceImpl boardCommandService;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
                .profileImage(ProfileImage.createProfileImage().fullPath("기본 이미지").build())
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        boardCommandService.create(member.getUsername(), form, null);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성")
    void addCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String title = "제목";
        Member member = memberRepository.findByEmail(email).get();
        Board board = boardRepository.findByTitle(title).get();

        CommentRequest.Create create = new CommentRequest.Create();
        create.setText("text1");
        //when
        CommentResponse.Create saved = commentCommendService.addComment(board.getId(), member.getUsername(), create);
        Comment findById = commentRepository.findById(saved.getCommentId()).orElseThrow(() -> new IllegalArgumentException(""));
        //then
        assertThat(findById.getId()).isEqualTo(saved.getCommentId());
        assertThat(findById.getText()).isEqualTo(create.getText());
        assertThat(findById.getBoard()).isEqualTo(board);
        assertThat(findById.getMember()).isEqualTo(board.getMember());
        assertThat(findById.getBoard().getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String title = "제목";
        Member member = memberRepository.findByEmail(email).get();
        Board board = boardRepository.findByTitle(title).get();

        CommentRequest.Create create = new CommentRequest.Create();
        create.setText("text1");

        commentCommendService.addComment(board.getId(), member.getUsername(), create);


        Comment comment = commentRepository.findByBoard(board).get();
        //when
        commentCommendService.deleteComment(comment.getId());

        //then
        assertThat(board.getCommentCount()).isEqualTo(0);
        assertThatThrownBy(() -> commentRepository.findById(comment.getId()).orElseThrow(() -> new IllegalArgumentException("")))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("댓글 수정")
    void modifyTest() throws Exception {
        //given
        String email = "tester@test.com";
        String title = "제목";
        Member member = memberRepository.findByEmail(email).get();
        Board board = boardRepository.findByTitle(title).get();

        CommentRequest.Create create = new CommentRequest.Create();
        create.setText("text1");

        CommentResponse.Create saved = commentCommendService.addComment(board.getId(), member.getUsername(), create);
        //when
        commentCommendService.modifyComment(saved.getCommentId(), "수정");
        Comment findById = commentRepository.findById(saved.getCommentId()).get();
        //then
        assertThat(findById.getText()).isEqualTo("수정");
    }


}