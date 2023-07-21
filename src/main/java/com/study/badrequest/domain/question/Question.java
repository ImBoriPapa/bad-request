package com.study.badrequest.domain.question;


import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.activity.ActivityScore;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.utils.markdown.MarkdownUtils;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question", indexes = {
        @Index(name = "QUESTION_EXPOSURE_IDX", columnList = "exposure")
})
@EqualsAndHashCode(of = "id")
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "title")
    private String title;
    @Column(name = "contents")
    @Lob
    private String contents;
    @Column(name = "preview")
    private String preview;
    @Enumerated(EnumType.STRING)
    @Column(name = "exposure")
    private ExposureStatus exposure;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_metrics_id")
    private QuestionMetrics questionMetrics;
    @Column(name = "asked_at")
    private LocalDateTime askedAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected Question(Member member, String title, String contents, String preview, ExposureStatus exposure, LocalDateTime askedAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        this.member = member;
        this.title = title;
        this.contents = contents;
        this.preview = preview;
        this.exposure = exposure;
        this.askedAt = askedAt;
        this.modifiedAt = modifiedAt;
        this.deletedAt = deletedAt;
    }

    public static Question createQuestion(String title, String contents, Member member, QuestionMetrics questionMetrics) {

        final String htmlContents = MarkdownUtils.parseMarkdownToHtml(contents);
        final String preview = makePreview(htmlContents);

        final Question question = Question.builder()
                .title(title)
                .contents(htmlContents)
                .preview(preview)
                .exposure(ExposureStatus.PUBLIC)
                .askedAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now().plusYears(10))
                .member(member)
                .build();

        question.addQuestionMetrics(questionMetrics);
        member.getMemberProfile().incrementActivityScore(ActivityScore.WRITE_QUESTION);

        return question;
    }

    public void addQuestionMetrics(QuestionMetrics questionMetrics) {
        this.questionMetrics = questionMetrics;
        questionMetrics.addQuestion(this);
    }

    private static String makePreview(String contents) {
        final String preview = MarkdownUtils.markdownToPlainText(contents);
        return preview.length() > 50 ? preview.substring(0, 50) : preview;
    }

    public void changeExposure(ExposureStatus status) {
        this.exposure = status;
        this.questionMetrics.changeExposure(status);
        this.deletedAt = LocalDateTime.now();
    }

    public void modify(String title, String contents) {
        this.title = title;
        this.contents = MarkdownUtils.parseMarkdownToHtml(contents);
        this.preview = makePreview(contents);
        this.modifiedAt = LocalDateTime.now();
    }
}
