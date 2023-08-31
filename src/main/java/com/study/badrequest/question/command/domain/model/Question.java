package com.study.badrequest.question.command.domain.model;

import com.study.badrequest.question.command.domain.dto.CreateQuestion;
import com.study.badrequest.question.command.domain.values.QuestionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.study.badrequest.question.command.domain.values.QuestionStatus.*;

@Getter
public final class Question {
    private final Long id;
    private final Writer writer;
    private final String title;
    private final String contents;
    private final CountOfRecommend countOfRecommend;
    private final CountOfUnRecommend countOfUnRecommend;
    private final CountOfView countOfView;
    private final CountOfAnswer countOfAnswer;
    private final QuestionStatus status;
    private final LocalDateTime askedAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Question(Long id, Writer writer, String title, String contents, CountOfRecommend countOfRecommend, CountOfUnRecommend countOfUnRecommend, CountOfView countOfView, CountOfAnswer countOfAnswer, QuestionStatus status, LocalDateTime askedAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.countOfRecommend = countOfRecommend;
        this.countOfUnRecommend = countOfUnRecommend;
        this.countOfView = countOfView;
        this.countOfAnswer = countOfAnswer;
        this.status = status;
        this.askedAt = askedAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Question createQuestion(CreateQuestion createQuestion) {

        return Question.builder()
                .writer(createQuestion.writer())
                .title(createQuestion.title())
                .contents(createQuestion.contents())
                .countOfRecommend(createQuestion.countOfRecommend())
                .countOfUnRecommend(createQuestion.countOfUnRecommend())
                .countOfView(createQuestion.countOfView())
                .countOfAnswer(createQuestion.countOfAnswer())
                .status(POSTED)
                .askedAt(LocalDateTime.now())
                .build();
    }

    public static Question initialize(InitializeQuestion initializeQuestion) {
        return Question.builder()
                .id(initializeQuestion.id())
                .writer(initializeQuestion.writer())
                .title(initializeQuestion.title())
                .contents(initializeQuestion.contents())
                .countOfRecommend(initializeQuestion.countOfRecommend())
                .countOfUnRecommend(initializeQuestion.countOfUnRecommend())
                .countOfView(initializeQuestion.countOfView())
                .countOfAnswer(initializeQuestion.countOfAnswer())
                .status(initializeQuestion.questionStatus())
                .askedAt(initializeQuestion.askedAt())
                .updatedAt(initializeQuestion.updatedAt())
                .deletedAt(initializeQuestion.deletedAt())
                .build();
    }

}
