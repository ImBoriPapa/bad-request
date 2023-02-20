package com.study.badrequest.domain.board.repository;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.service.BoardCommandService;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class BoardQueryRepositoryImplTest extends BaseMemberTest {

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

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    BoardCommandService boardCommandService;

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
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("게시판 검색")
    void search() throws Exception {
        //given
        String email = "tester@test.com";

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(""));
        for (int i = 1; i <= 40; i++) {
            BoardRequest.Create form = BoardRequest.Create
                    .builder()
                    .title("제목" + i)
                    .category(Category.KNOWLEDGE)
                    .contents("내용" + i)
                    .topic(Topic.JAVA)
                    .build();
            boardCommandService.create(member.getUsername(), form, null);
        }
        //when
        BoardSearchCondition condition1 = new BoardSearchCondition(40, 0L, null, null, null, null, null);

        List<BoardListResult> results1 = boardQueryRepositoryImpl.findBoardList(condition1)
                .getResults();

        //then
        assertThat(results1.size()).isEqualTo(40);


    }

    @Test
    @DisplayName("게시판 상세 검색1")
    void searchDetail1() throws Exception {
        //given
        String email = "tester@test.com";


        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(""));

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

        //when

        BoardDetailDto boardDetail = boardQueryRepositoryImpl.findBoardDetail(save.getId(), null)
                .orElseThrow(() -> new IllegalArgumentException("검색 결과 없음"));

        //then
        assertThat(boardDetail.getBoardId()).isEqualTo(save.getId());
        assertThat(boardDetail.getMemberId()).isEqualTo(save.getMember().getId());
        assertThat(boardDetail.getTitle()).isEqualTo(save.getTitle());
        assertThat(boardDetail.getContents()).isEqualTo(save.getContents());
    }


}