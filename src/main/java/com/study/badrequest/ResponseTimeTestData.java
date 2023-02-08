package com.study.badrequest;

import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.entity.ProfileImage;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Transactional
@Profile("dev")
@Slf4j
public class ResponseTimeTestData {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @PostConstruct
    public void init() {
        boardData();
    }

    public void boardData() {
        log.info("[INIT SAMPLE BOARD START]");
        Member member = Member.createMember()
                .email("email@email.com")
                .build();

        Member save = memberRepository.save(member);

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

}
