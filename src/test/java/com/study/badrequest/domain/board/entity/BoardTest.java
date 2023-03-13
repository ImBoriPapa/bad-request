package com.study.badrequest.domain.board.entity;

import com.study.badrequest.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoardTest {


    @Test
    @DisplayName("Board 생성")
    void createBoardTest() throws Exception {
        //given
        String title = "title";

        String contents = "내용내용내용내용내용";

        Category category = Category.KNOWLEDGE;
        Topic topic = Topic.JAVA;

        Member member = Member.createMember().email("email@email.com").build();

        //when
        Board board = Board.createBoard()
                .title(title)
                .contents(contents)
                .category(category)
                .topic(topic)
                .member(member)
                .build();
        //then
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getContents()).isEqualTo(contents);
        assertThat(board.getCategory()).isEqualTo(category);
        assertThat(board.getTopic()).isEqualTo(topic);
    }

    @Test
    @DisplayName("Board 수정")
    void updateBoardTest() throws Exception {
        //given
        String title = "title";

        String contents = "내용내용내용내용내용";

        Category category = Category.KNOWLEDGE;
        Topic topic = Topic.JAVA;

        Board board = Board.createBoard()
                .title(title)
                .contents(contents)
                .category(category)
                .topic(topic)
                .build();
        //when
        board.titleUpdateIfHasChange(""); // 제목에 공백 입력시 수정 반영 x
        board.contentsUpdateIfNotNull(null); // 내용에 null 입력시 수정 반영 x
        //then
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getContents()).isEqualTo(contents);
    }

}