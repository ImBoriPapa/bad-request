package com.study.badrequest.question.command.domain;

import com.study.badrequest.hashtag.command.domain.HashTag;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "question_tag")
@EqualsAndHashCode(of = "id")
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_tag_id")
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    public Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_tag_id")
    private HashTag hashTag;

    protected QuestionTag(Question question, HashTag hashTag) {
        this.question = question;
        this.hashTag = hashTag;
    }

    public static QuestionTag createQuestionTag(Question question, HashTag hashTag) {
        hashTag.incrementUsage();
        return new QuestionTag(question, hashTag);
    }
}
