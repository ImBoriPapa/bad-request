package com.study.badrequest;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;


@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SampleBoardData {
    private final String[] JAVA_TITLES = {"자바 기초", "객체 지향 프로그래밍", "자바 컬렉션", "자바 스트림", "자바 병렬 처리"};
    private final String[] JAVA_CONTENTS = {"자바 프로그래밍 기초 배우기", "자바에서의 객체 지향 원리 이해하기", "자바 컬렉션 클래스 탐구하기", "자바 스트림 마스터하기", "자바에서의 병렬 처리 알아보기"};
    private final String[] JS_TITLES = {"JavaScript 기초", "DOM 조작", "jQuery 기초", "React Hooks", "Node.js와 Express"};
    private final String[] JS_CONTENTS = {"JavaScript 기초 배우기", "JavaScript를 이용한 DOM 조작", "jQuery 시작하기", "React Hooks 탐구하기", "Node.js와 Express로 웹 앱 만들기"};
    private final String[] PYTHON_TITLES = {"파이썬 기초", "파이썬 데이터 과학", "파이썬 웹 개발"};
    private final String[] MYSQL_TITLES = {"MySQL 기초", "고급 MySQL 기술", "MySQL 관리"};
    private final String[] MONGODB_TITLES = {"MongoDB 기초", "MongoDB 집계 파이프라인", "MongoDB Atlas 관리"};
    private final String[] PYTHON_CONTENTS = {"파이썬 프로그래밍 기초 배우기", "파이썬 데이터 과학 탐구하기", "파이썬으로 웹 앱 만들기"};
    private final String[] MYSQL_CONTENT = {"MySQL 시작하기", "고급 MySQL 기술 배우기", "MySQL 관리자가 되기"};
    private final String[] MONGODB_CONTENT = {"MongoDB 알아보기", "MongoDB 집계 파이프라인 마스터하기", "MongoDB Atlas 관리하기"};
    private final Random random = new Random();
    private final BoardRepository boardRepository;
    public void initSampleBoards(Member member, int size) {

        ArrayList<Board> list = new ArrayList<>(size);
        IntStream.rangeClosed(1, size).forEach(index -> {
            Board board = Board.createBoard()
                    .title(generateTitle())
                    .contents(generateContents())
                    .category(generateCategory())
                    .topic(generateTopic())
                    .member(member)
                    .build();
            list.add(board);
        });
        boardRepository.saveAll(list);
    }

    private String generateTitle() {
        String[] titles = generateTitles();
        return titles[random.nextInt(titles.length)];
    }

    private String generateContents() {
        String[] contents = generateContent();
        return contents[random.nextInt(contents.length)];
    }

    private Category generateCategory() {
        Category[] categories = Category.values();
        return categories[random.nextInt(categories.length)];
    }

    private Topic generateTopic() {
        Topic[] topics = Topic.values();
        return topics[random.nextInt(topics.length)];
    }

    private String[] generateTitles() {
        Topic topic = generateTopic();
        switch (topic) {
            case JAVA:
                return JAVA_TITLES;
            case JAVASCRIPT:
                return JS_TITLES;
            case PYTHON:

                return PYTHON_TITLES;
            case MYSQL:

                return MYSQL_TITLES;
            case MONGODB:

                return MONGODB_TITLES;
            default:
                return new String[]{"Default Title"};
        }
    }

    private String[] generateContent() {
        Topic topic = generateTopic();
        switch (topic) {
            case JAVA:
                return JAVA_CONTENTS;
            case JAVASCRIPT:
                return JS_CONTENTS;
            case PYTHON:

                return PYTHON_CONTENTS;
            case MYSQL:

                return MYSQL_CONTENT;
            case MONGODB:

                return MONGODB_CONTENT;
            default:
                return new String[]{"Default Content"};
        }
    }


}
