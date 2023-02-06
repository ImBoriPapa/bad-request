package com.study.badrequest;

import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.entity.ProfileImage;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.entity.SubComment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.entity.Comment;

import com.study.badrequest.domain.comment.repository.SubCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
@Profile({"test", "dev", "prod"})
public class TestSampleData {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;

    private final BoardImageRepository boardImageRepository;
    private final CommentRepository commentRepository;

    private final SubCommentRepository subCommentRepository;


    public static final String SAMPLE_USER_EMAIL = "user@gmail.com";
    public static final String SAMPLE_USER_CONTACT = "010-0000-1234";
    public static final String SAMPLE_TEACHER_EMAIL = "teacher@gmail.com";
    public static final String SAMPLE_ADMIN_EMAIL = "admin@gmail.com";
    public static final String SAMPLE_PASSWORD = "password1234!@";


    @PostConstruct
    public void initSampleDataWhenDevelopmentAndTest() {
        initSampleUser();
        initSampleBoard();
        initSampleComment();
    }

    public void initSampleUser() {
        log.info("[INIT SAMPLE USER START]");

        Member user = Member.createMember()
                .email(SAMPLE_USER_EMAIL)
                .nickname("SAMPLE_USER")
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .contact(SAMPLE_USER_CONTACT)
                .authority(Member.Authority.MEMBER)
                .build();

        Member teacher = Member.createMember()
                .email(SAMPLE_TEACHER_EMAIL)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .authority(Member.Authority.TEACHER)
                .build();

        Member admin = Member.createMember()
                .email(SAMPLE_ADMIN_EMAIL)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .authority(Member.Authority.ADMIN)
                .build();
        memberRepository.saveAll(List.of(user, teacher, admin));
        log.info("[INIT SAMPLE USER FINISH]");
    }


    public void clearSampleUser() {
        log.info("[CLEAR SAMPLE USER FINISH]");
        memberRepository.deleteAll();
        log.info("[CLEAR SAMPLE USER FINISH]");
    }

    public void initSampleBoard() {
        log.info("[INIT SAMPLE BOARD START]");
        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException(""));

        List<Board> list1 = new ArrayList<>();
        List<Board> list2 = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            Board board = Board.createBoard()
                    .title("title" + i)
                    .contents("tototototot")
                    .category(Category.KNOWLEDGE)
                    .topic(Topic.JAVA)
                    .member(member)
                    .build();
            list1.add(board);
        }

        for (int i = 21; i <= 40; i++) {
            ProfileImage profileImage = ProfileImage.builder()
                    .fullPath("http://localhost:8080/image/image.png")
                    .build();

            Board board = Board.createBoard()
                    .title("title" + i)
                    .contents("tototototot")
                    .category(Category.QUESTION)
                    .topic(Topic.MYSQL)
                    .member(member)
                    .build();
            list1.add(board);
        }
        Board board = Board.createBoard()
                .title("Webflux")
                .contents("tototototot")
                .category(Category.QUESTION)
                .topic(Topic.JAVA)
                .member(member)
                .build();
        boardRepository.save(board);
        boardRepository.saveAll(list1);
        boardRepository.saveAll(list2);
        log.info("[INIT SAMPLE BOARD FINISH]");
    }

    @Transactional
    public void initSampleComment() {
        log.info("[INIT SAMPLE COMMENT START]");

        Board board = boardRepository.findByTitle("title1").get();


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


        commentRepository.saveAll(List.of(comment1, comment2, comment3));
        Comment parentComment = commentRepository.findById(comment1.getId()).get();
        SubComment subComment1 = SubComment.CreateSubComment()
                .text("sub 1")
                .board(board)
                .member(board.getMember())
                .comment(parentComment)
                .build();

        SubComment subComment2 = SubComment.CreateSubComment()
                .text("sub 2")
                .board(board)
                .member(board.getMember())
                .comment(parentComment)
                .build();

        SubComment subComment3 = SubComment.CreateSubComment()
                .text("sub 3")
                .board(board)
                .member(board.getMember())
                .comment(parentComment)
                .build();
        subCommentRepository.saveAll(List.of(subComment3, subComment2, subComment1));
        log.info("[INIT SAMPLE COMMENT FINISH]");
    }
}