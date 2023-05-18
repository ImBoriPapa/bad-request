package com.study.badrequest.domain.question;


import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.utils.markdown.MarkdownUtils;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION", indexes = {
        @Index(name = "QUESTION_EXPOSURE_IDX", columnList = "EXPOSURE")
}
)
@EqualsAndHashCode(of = "id")
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "CONTENTS")
    @Lob
    private String contents;
    @Column(name = "PREVIEW")
    private String preview;
    @Enumerated(EnumType.STRING)
    @Column(name = "EXPOSURE")
    private ExposureStatus exposure;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "QUESTION_METRICS_ID")
    private QuestionMetrics questionMetrics;
    @Column(name = "ASKED_DATE_TIME")
    private LocalDateTime askedAt;
    @Column(name = "MODIFIED_DATE_TIME")
    private LocalDateTime modifiedAt;
    @Column(name = "DELETED_REQUEST_DATE_TIME")
    private LocalDateTime deletedRequestAt;

    @Builder(builderMethodName = "createQuestion")
    public Question(Member member, String title, String contents) {
        this.member = member;
        this.title = title;
        this.contents = setContents(contents);
        this.preview = setPreview(contents);
        this.exposure = ExposureStatus.PUBLIC;
        this.askedAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.deletedRequestAt = null;
    }

    public void addQuestionMetrics(QuestionMetrics questionMetrics) {
        this.questionMetrics = questionMetrics;
    }

    private String setContents(String contents) {
        return MarkdownUtils.parseMarkdownToHtml(contents);
    }

    private String setPreview(String contents) {
        String plainText = MarkdownUtils.markdownToPlainText(contents);
        if (plainText.length() > 50) {
            return plainText.substring(0, 50) + "...";
        } else {
            return plainText;
        }
    }

    public void changeExposureToDelete(ExposureStatus status) {
        this.exposure = status;
        this.questionMetrics.changeExposure(status);
        this.deletedRequestAt = LocalDateTime.now();
    }

    public void modify(String title, String contents) {
        this.title = title;
        this.contents = setContents(contents);
        this.preview = setPreview(contents);
        this.modifiedAt = LocalDateTime.now();
    }
}
