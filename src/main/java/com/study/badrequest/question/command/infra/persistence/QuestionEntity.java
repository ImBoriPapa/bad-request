package com.study.badrequest.question.command.infra.persistence;


import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question", indexes = {
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
    private Integer countOfRecommend;
    private Integer countOfUnRecommend;
    private Integer countOfView;
    private Integer countOfAnswer;
    @Column(name = "asked_at")
    private LocalDateTime askedAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected QuestionEntity(Long id, WriterEntity writer, String title, String contents, QuestionStatus questionStatus, Integer countOfRecommend, Integer countOfUnRecommend, Integer countOfView, Integer countOfAnswer, LocalDateTime askedAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
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
                .countOfRecommend(question.getCountOfRecommend())
                .countOfUnRecommend(question.getCountOfUnRecommend())
                .countOfView(question.getCountOfView())
                .countOfAnswer(question.getCountOfAnswer())
                .askedAt(question.getAskedAt())
                .build();
    }

    public Question toModel() {
        return Question.initialize();
    }
}
