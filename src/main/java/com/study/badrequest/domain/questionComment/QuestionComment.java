package com.study.badrequest.domain.questionComment;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "QUESTION_COMMENT")
@Getter
public class QuestionComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_COMMENT_ID")
    private Long id;
    @Column(name = "CONTENTS")
    private String contents;
    @Column(name = "EXPOSURE_STATUS")
    @Enumerated(EnumType.STRING)
    private ExposureStatus exposureStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member writer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
    @Column(name = "ADDED_AT")
    private LocalDateTime addedAt;
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Builder
    public QuestionComment(String contents, ExposureStatus exposureStatus, Member writer, Question question, LocalDateTime addedAt, LocalDateTime deletedAt) {
        this.contents = contents;
        this.exposureStatus = exposureStatus;
        this.writer = writer;
        this.question = question;
        this.addedAt = addedAt;
        this.deletedAt = deletedAt;
    }

    public void statusToDelete() {
        this.exposureStatus = ExposureStatus.DELETE;
        this.deletedAt = LocalDateTime.now();
    }
}