package com.study.badrequest.domain.board.service;



import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;


import com.study.badrequest.domain.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.User;

import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class BoardCommandServiceTest {
    @InjectMocks
    private BoardCommandServiceImpl boardCommandService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardImageService boardImageService;

    @Test
    @DisplayName("board 생성")
    void createTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password("password1234")
                .authority(Authority.MEMBER)
                .build();
        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        Board board = Board.createBoard().build();
        User user = new User(UUID.randomUUID().toString(), member.getPassword(), Authority.MEMBER.getAuthorities());
        //when
        when(memberRepository.findMemberByUsernameAndAuthority(any(), any())).thenReturn(Optional.of(member));
        when(boardRepository.save(any())).thenReturn(board);
        BoardResponse.Create response = boardCommandService.create(user, form);
        //then
        assertThat(response.getCreateAt()).isNotNull();


    }

    @Test
    @DisplayName("게시판 수정")
    void updateBoard() throws Exception {
        //given
        BoardRequest.Update form = BoardRequest.Update
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        Member member = Member.createMember()
                .email("email@email.com")
                .password("password1234")
                .authority(Authority.MEMBER)
                .build();
        member.replaceUsername();
        Board board = Board.createBoard()
                .build();
        User user = new User(member.getUsername(), member.getPassword(), member.getAuthority().getAuthorities());
        //when
        when(memberRepository.findMemberByUsernameAndAuthority(any(), any())).thenReturn(Optional.of(member));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        BoardResponse.Update response = boardCommandService.update(user, 1L, form);
        //then
        assertThat(response.getUpdatedAt()).isNotNull();
    }
}