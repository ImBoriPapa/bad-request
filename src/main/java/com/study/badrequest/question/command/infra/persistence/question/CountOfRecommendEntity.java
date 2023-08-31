package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfRecommend;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CountOfRecommendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long count;

    protected CountOfRecommendEntity(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfRecommendEntity fromModel(CountOfRecommend countOfRecommend) {
        return new CountOfRecommendEntity(countOfRecommend.getId(), countOfRecommend.getCount());
    }

    public CountOfRecommend toModel() {
        return CountOfRecommend.initialize(getId(), getCount());
    }
}
