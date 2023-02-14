package com.study.badrequest;

import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.entity.SubComment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.repository.SubCommentRepository;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

import static com.study.badrequest.SampleUserData.*;

@Component
@RequiredArgsConstructor
@Transactional
@Profile({"dev", "prod"})
@Slf4j
public class DevSampleTestData {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;

    @PostConstruct
    public void init() {
        sampleMember();
        sampleBoardData();
        initSampleComment();
    }

    @PreDestroy
    public void reSet() {
        memberRepository.deleteAll();
        boardRepository.deleteAll();
        commentRepository.deleteAll();
        subCommentRepository.deleteAll();
    }

    public void sampleMember() {
        ProfileImage defaultProfileImage = ProfileImage.builder()
                .fullPath("https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/profile.jpg").build();

        Member user = Member.createMember()
                .email(SAMPLE_USER_EMAIL)
                .nickname(SAMPLE_USER_NICKNAME)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .contact(SAMPLE_USER_CONTACT)
                .authority(Authority.MEMBER)
                .profileImage(defaultProfileImage)
                .build();

        Member teacher = Member.createMember()
                .email(SAMPLE_TEACHER_EMAIL)
                .nickname(SAMPLE_TEACHER_NICKNAME)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .authority(Authority.TEACHER)
                .profileImage(defaultProfileImage)
                .build();

        Member admin = Member.createMember()
                .email(SAMPLE_ADMIN_EMAIL)
                .nickname(SAMPLE_ADMIN_NICKNAME)
                .contact(SAMPLE_ADMIN_CONTACT)
                .password(passwordEncoder.encode(SAMPLE_PASSWORD))
                .authority(Authority.ADMIN)
                .profileImage(defaultProfileImage)
                .build();

        Member saveUser = memberRepository.save(user);
        Member saveTeacher = memberRepository.save(teacher);
        Member saveAdmin = memberRepository.save(admin);
    }

    public void sampleBoardData() {
        log.info("[INIT SAMPLE BOARD START]");

        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL).get();

        List<Board> categoryNotice = new ArrayList<>();
        List<Board> categoryQuestion = new ArrayList<>();
        List<Board> categoryKnowledge = new ArrayList<>();
        List<Board> categoryCommunity = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            Board board = Board.createBoard()
                    .title("sample" + i)
                    .contents("tototototot")
                    .category(Category.NOTICE)
                    .topic(Topic.JAVA)
                    .member(member)
                    .build();
            categoryNotice.add(board);
        }

        for (int i = 1001; i <= 2000; i++) {
            Board board = Board.createBoard()
                    .title("sample" + i)
                    .contents("tototototot")
                    .category(Category.QUESTION)
                    .topic(Topic.MYSQL)
                    .member(member)
                    .build();
            categoryNotice.add(board);
        }
        for (int i = 2001; i <= 3000; i++) {
            Board board = Board.createBoard()
                    .title("sample" + i)
                    .contents("tototototot")
                    .category(Category.KNOWLEDGE)
                    .topic(Topic.MYSQL)
                    .member(member)
                    .build();
            categoryNotice.add(board);
        }
        for (int i = 3001; i <= 4000; i++) {
            Board board = Board.createBoard()
                    .title("sample" + i)
                    .contents("tototototot")
                    .category(Category.COMMUNITY)
                    .topic(Topic.MYSQL)
                    .member(member)
                    .build();
            categoryNotice.add(board);
        }

        boardRepository.saveAll(categoryNotice);
        boardRepository.saveAll(categoryQuestion);
        boardRepository.saveAll(categoryKnowledge);
        boardRepository.saveAll(categoryCommunity);

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
