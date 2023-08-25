package com.study.badrequest.question.command.domain;

import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.question.command.domain.dto.CreateQuestion;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.study.badrequest.question.command.domain.QuestionStatus.*;

@Getter
public final class Question {
    private final Long id;
    private final Writer writer;
    private final String title;
    private final String contents;
    private final Integer countOfRecommend;
    private final Integer countOfUnRecommend;
    private final Integer countOfView;
    private final Integer countOfAnswer;
    private final QuestionStatus status;
    private final LocalDateTime askedAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PUBLIC)
    public Question(Long id, Writer writer, String title, String contents, Integer countOfRecommend, Integer countOfUnRecommend, Integer countOfView, Integer countOfAnswer, QuestionStatus status, LocalDateTime askedAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
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

    public static Question createQuestion(CreateQuestion createQuestion, Writer writer) {

        return Question.builder()
                .writer(writer)
                .title(createQuestion.title())
                .contents(createQuestion.contents())
                .countOfRecommend(0)
                .countOfUnRecommend(0)
                .countOfView(0)
                .countOfAnswer(0)
                .status(POSTED)
                .askedAt(LocalDateTime.now())
                .build();
    }

    public static Question initialize() {
        return Question.builder().build();
    }

}
