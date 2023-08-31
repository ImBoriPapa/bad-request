package com.study.badrequest.question.command.infra.persistence.question;


import com.study.badrequest.question.command.domain.model.InitializeQuestion;
import com.study.badrequest.question.command.domain.model.Question;
import com.study.badrequest.question.command.domain.values.QuestionStatus;
import com.study.badrequest.question.command.infra.persistence.writer.WriterEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "questions", indexes = {
        @Index(name = "STATUS_IDX", columnList = "questionStatus")
})
@EqualsAndHashCode(of = "id")
@Getter
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private WriterEntity writer;
    @Column(name = "title")
    private String title;
    @Column(name = "contents")
    @Lob
    private String contents;
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus;
    @OneToOne(fetch = FetchType.LAZY)
    private CountOfRecommendEntity countOfRecommend;
    @OneToOne(fetch = FetchType.LAZY)
    private CountOfUnRecommendEntity countOfUnRecommend;
    @OneToOne(fetch = FetchType.LAZY)
    private CountOfViewEntity countOfView;
    @OneToOne(fetch = FetchType.LAZY)
    private CountOfAnswerEntity countOfAnswer;
    @Column(name = "asked_at")
    private LocalDateTime askedAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected QuestionEntity(Long id, WriterEntity writer, String title, String contents, QuestionStatus questionStatus, CountOfRecommendEntity countOfRecommend, CountOfUnRecommendEntity countOfUnRecommend, CountOfViewEntity countOfView, CountOfAnswerEntity countOfAnswer, LocalDateTime askedAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.questionStatus = questionStatus;
        this.countOfRecommend = countOfRecommend;
        this.countOfUnRecommend = countOfUnRecommend;
        this.countOfView = countOfView;
        this.countOfAnswer = countOfAnswer;
        this.askedAt = askedAt;
        this.modifiedAt = modifiedAt;
        this.deletedAt = deletedAt;
    }

    public static QuestionEntity formModel(Question question) {
        return QuestionEntity.builder()
                .title(question.getTitle())
                .contents(question.getContents())
                .writer(WriterEntity.fromModel(question.getWriter()))
                .questionStatus(question.getStatus())
                .countOfRecommend(CountOfRecommendEntity.fromModel(question.getCountOfRecommend()))
                .countOfUnRecommend(CountOfUnRecommendEntity.fromModel(question.getCountOfUnRecommend()))
                .countOfView(CountOfViewEntity.fromModel(question.getCountOfView()))
                .countOfAnswer(CountOfAnswerEntity.fromModel(question.getCountOfAnswer()))
                .askedAt(question.getAskedAt())
                .build();
    }

    public Question toModel() {
        InitializeQuestion initializeQuestion = InitializeQuestion.builder()
                .id(getId())
                .writer(getWriter().toModel())
                .title(getTitle())
                .contents(getContents())
                .countOfRecommend(getCountOfRecommend().toModel())
                .countOfUnRecommend(getCountOfUnRecommend().toModel())
                .countOfView(getCountOfView().toModel())
                .countOfAnswer(getCountOfAnswer().toModel())
                .questionStatus(getQuestionStatus())
                .askedAt(getAskedAt())
                .updatedAt(getAskedAt())
                .deletedAt(getDeletedAt())
                .build();

        return Question.initialize(initializeQuestion);
    }
}
