package com.study.badrequest.answer.command.domain;

import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
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
    @Enumerated(EnumType.STRING)
    private ExposureStatus exposureStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity writer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;
    @Column(name = "ADDED_AT")
    private LocalDateTime addedAt;
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Builder
    public AnswerComment(String contents, ExposureStatus exposureStatus, MemberEntity writer, Answer answer, LocalDateTime addedAt, LocalDateTime deletedAt) {
        this.contents = contents;
        this.exposureStatus = exposureStatus;
        this.writer = writer;
        this.answer = answer;
        this.addedAt = addedAt;
        this.deletedAt = deletedAt;
    }

    public void statusToDelete() {
        this.exposureStatus = ExposureStatus.DELETE;
        this.deletedAt = LocalDateTime.now();
    }
}
