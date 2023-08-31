package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfAnswer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CountOfAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long count;

    protected CountOfAnswerEntity(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfAnswerEntity fromModel(CountOfAnswer countOfAnswer) {
        return new CountOfAnswerEntity(countOfAnswer.getId(), countOfAnswer.getCount());
    }

    public CountOfAnswer toModel() {
        return new CountOfAnswer(getId(),getCount());
    }
}
