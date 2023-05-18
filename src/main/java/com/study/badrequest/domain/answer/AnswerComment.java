package com.study.badrequest.domain.answer;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.question.Question;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "ANSWER_COMMENT")
@Getter
public class AnswerComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_COMMENT_ID")
    private Long id;
    @Column(name = "CONTENTS")
    private String contents;
    @Column(name = "EXPOSURE_STATUS")
    private ExposureStatus exposureStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_PROFILE_ID")
    private MemberProfile writer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
    private LocalDateTime addedAt;
    private LocalDateTime deletedAt;
    @Builder
    public AnswerComment(String contents, ExposureStatus exposureStatus, MemberProfile writer, Question question, LocalDateTime addedAt, LocalDateTime deletedAt) {
        this.contents = contents;
        this.exposureStatus = exposureStatus;
        this.writer = writer;
        this.question = question;
        this.addedAt = addedAt;
        this.deletedAt = deletedAt;
    }
}
