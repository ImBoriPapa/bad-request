package com.study.badrequest.domain.question;

import com.study.badrequest.domain.hashTag.HashTag;
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
        hashTag.incrementUsage();
    }

    public static QuestionTag createQuestionTag(Question question, HashTag hashTag) {
        return new QuestionTag(question, hashTag);
    }
}
