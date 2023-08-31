package com.study.badrequest.answer.command.domain;

import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;

import com.study.badrequest.question.command.infra.persistence.question.QuestionEntity;
import com.study.badrequest.utils.markdown.MarkdownUtils;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ANSWER")
@EqualsAndHashCode(of = "id")
@Getter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_ID")
    private Long id;
    @Column(name = "CONTENTS")
    @Lob
    private String contents;
    @Column(name = "NUMBER_OF_RECOMMENDATION")
    private Integer numberOfRecommendation;
    @Column(name = "NUMBER_OF_COMMENT")
    private Integer numberOfComment;
    @Column(name = "EXPOSURE_STATUS")
    @Enumerated(EnumType.STRING)
    private ExposureStatus exposureStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private QuestionEntity question;
    @Column
    private LocalDateTime answeredAt;
    @Column
    private LocalDateTime modifiedAt;
    @Column
    private LocalDateTime deletedAt;

    @Builder(builderMethodName = "createAnswer")
    public Answer(String contents, MemberEntity member, QuestionEntity question) {
        this.contents = MarkdownUtils.parseMarkdownToHtml(contents);
        this.numberOfRecommendation = 0;
        this.numberOfComment = 0;
        this.exposureStatus = ExposureStatus.PUBLIC;
        this.member = member;
        this.question = question;
        this.answeredAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void modifyContents(String contents) {
        this.contents = MarkdownUtils.parseMarkdownToHtml(contents);
        this.modifiedAt = LocalDateTime.now();
    }

    public void statusToDelete() {
        this.exposureStatus = ExposureStatus.DELETE;
        this.deletedAt = LocalDateTime.now();
    }

    public void incrementRecommendation() {
        ++this.numberOfRecommendation;
    }

    public void decrementRecommendation() {
        --this.numberOfRecommendation;
    }
}
