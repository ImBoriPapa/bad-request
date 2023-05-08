package com.study.badrequest.domain.question;

import com.study.badrequest.domain.board.HashTag;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "QUESTION_TAG")
@EqualsAndHashCode(of = "id")
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_TAG_ID")
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    public Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HASHTAG_ID")
    private HashTag hashTag;

    protected QuestionTag(Question question,HashTag hashTag) {
        this.question = question;
        this.hashTag = hashTag;
    }

    public static QuestionTag createQuestionTag(Question question, HashTag hashTag) {
        return new QuestionTag(question,hashTag);
    }
}
