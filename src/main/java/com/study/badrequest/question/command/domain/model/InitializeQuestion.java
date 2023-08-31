package com.study.badrequest.question.command.domain.model;

import com.study.badrequest.question.command.domain.values.QuestionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record InitializeQuestion(Long id, Writer writer, String title, String contents, CountOfRecommend countOfRecommend,
                                 CountOfUnRecommend countOfUnRecommend, CountOfView countOfView, CountOfAnswer countOfAnswer,
                                 QuestionStatus questionStatus, LocalDateTime askedAt, LocalDateTime updatedAt,
                                 LocalDateTime deletedAt) {
    @Builder
    public InitializeQuestion {
    }
}