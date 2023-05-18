package com.study.badrequest.domain.answer;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.Member;

import com.study.badrequest.domain.question.Question;
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
    @Column(name = "EXPOSURE_STATUS")
    @Enumerated(EnumType.STRING)
    private ExposureStatus exposureStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
    @Column
    private LocalDateTime answeredAt;
    @Column
    private LocalDateTime modifiedAt;
    @Column
    private LocalDateTime deletedAt;

    @Builder(builderMethodName = "createAnswer")
    public Answer(String contents, Member member, Question question) {
        this.contents = contents;
        this.numberOfRecommendation = 0;
        this.exposureStatus = ExposureStatus.PUBLIC;
        this.member = member;
        this.question = question;
        this.answeredAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateContents(String contents) {
        this.contents = contents;
        this.modifiedAt = LocalDateTime.now();
    }

    public void statusToDelete() {
        this.exposureStatus = ExposureStatus.DELETE;
        this.deletedAt = LocalDateTime.now();
    }
}
