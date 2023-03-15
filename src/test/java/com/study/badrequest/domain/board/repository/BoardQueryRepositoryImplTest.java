package com.study.badrequest.domain.board.repository;

import com.study.badrequest.TestConfig;

import com.study.badrequest.SampleBoardData;
import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.query.BoardDetailDto;
import com.study.badrequest.domain.board.repository.query.BoardListDto;
import com.study.badrequest.domain.board.repository.query.BoardQueryRepositoryImpl;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Slf4j
@ActiveProfiles("test")
@Import({TestConfig.class, BoardQueryRepositoryImpl.class, SampleBoardData.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardQueryRepositoryImplTest {

    @Autowired
    private BoardQueryRepositoryImpl boardQueryRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SampleBoardData testSampleBoardData;

    @Test
    @DisplayName("게시판 상세 조회 테스트")
    void 상세조회() throws Exception {
        //given
        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("https://localhost:8080/fulpath.png")
                .build();

        Member member = Member.createMember()
                .email("email@email.com")
                .nickname("nickname")
                .password("password1234!@")
                .contact("010-1213-1313")
                .authority(Authority.MEMBER)
                .profileImage(profileImage)
                .build();
        Member saved = memberRepository.save(member);

        Board board = Board.createBoard()
                .title("제목")
                .contents("내용내용내용")
                .category(Category.KNOWLEDGE)
                .topic(Topic.JAVA)
                .member(saved)
                .build();
        Board save = boardRepository.save(board);
        //when
        BoardDetailDto detail = boardQueryRepository.findBoardDetailByIdAndCategory(save.getId(), save.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Not Found Board Detail"));
        //then
        assertThat(detail.getId()).isEqualTo(save.getId());

    }

    @Test
    @DisplayName("게시판 리스트 조건 없이 조회")
    void 리스트조회() throws Exception {
        //given
        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("https://localhost:8080/fulpath.png")
                .build();

        Member member = Member.createMember()
                .email("email1@email.com")
                .nickname("일반회원")
                .password("password1234!@")
                .contact("010-1213-1111")
                .authority(Authority.MEMBER)
                .profileImage(profileImage)
                .build();

        Member saved = memberRepository.save(member);

        testSampleBoardData.initSampleBoards(saved, 15); //게시판 15개 저장

        BoardSearchCondition condition = new BoardSearchCondition(); //조건 x default size=10,
        //when
        BoardListDto result = boardQueryRepository.findBoardList(condition);
        //then
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getLastIndex()).isEqualTo(6);
        assertThat(result.getResults()).isNotEmpty();
    }

    @Test
    @DisplayName("게시판 리스트 제목 검색")
    void 제목검색() throws Exception{
        //given
        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("https://localhost:8080/fulpath.png")
                .build();

        Member member = Member.createMember()
                .email("email1@email.com")
                .nickname("일반회원")
                .password("password1234!@")
                .contact("010-1213-1111")
                .authority(Authority.MEMBER)
                .profileImage(profileImage)
                .build();

        Member saved = memberRepository.save(member);

        testSampleBoardData.initSampleBoards(saved, 15); //게시판 15개 저장

        String keyword = "MySQL";
        //조건 size=10,lastIndex=null,제목은= MySQL,category= null,topic=null,nickname null
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setTitle(keyword);
        //when
        BoardListDto result = boardQueryRepository.findBoardList(condition);
        //then
        assertThat(result.getSize()).isNotZero();
    }

    @Test
    @DisplayName("게시판 카테고리 별로 조회")
    void 카테고리검색() throws Exception{
        //given
        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("https://localhost:8080/fulpath.png")
                .build();

        Member member = Member.createMember()
                .email("email1@email.com")
                .nickname("일반회원")
                .password("password1234!@")
                .contact("010-1213-1111")
                .authority(Authority.MEMBER)
                .profileImage(profileImage)
                .build();

        Member saved = memberRepository.save(member);
        testSampleBoardData.initSampleBoards(saved, 15); //게시판 15개 저장
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setCategory(Category.KNOWLEDGE);
        //when
        BoardListDto result = boardQueryRepository.findBoardList(condition);
        //then
        assertThat(result.getResults().get(0).getCategory()).isEqualTo(Category.KNOWLEDGE);

    }

}