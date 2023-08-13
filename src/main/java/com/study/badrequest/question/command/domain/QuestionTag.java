package com.study.badrequest.question.command.domain;

import com.study.badrequest.hashtag.command.domain.Tag;
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
    @JoinColumn(name = "tag_id")
    private Tag tag;

    protected QuestionTag(Tag tag) {
        this.tag = tag;
    }

    public static QuestionTag createQuestionTag(Tag tag) {
        return new QuestionTag(tag);
    }

    public void assignQuestion(Question question) {

        if (this.question != null) {
            this.getQuestion().getQuestionTags().remove(this);
        }

        this.question = question;

        question.getQuestionTags().add(this);
    }
}
