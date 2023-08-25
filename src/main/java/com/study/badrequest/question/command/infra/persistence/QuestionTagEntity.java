package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.QuestionTag;
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
public class QuestionTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_tag_id")
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    public QuestionEntity question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    protected QuestionTagEntity(Long id, QuestionEntity question, TagEntity tag) {
        this.id = id;
        this.question = question;
        this.tag = tag;
    }

    public static QuestionTagEntity fromModel(QuestionTag questionTag) {
        return new QuestionTagEntity(
                questionTag.getId(),
                QuestionEntity.formModel(questionTag.getQuestion()),
                TagEntity.fromModel(questionTag.getTag()));
    }

    public QuestionTag toModel() {
        return QuestionTag.initialize(getId(), getQuestion().toModel(), getTag().toModel());
    }
}
