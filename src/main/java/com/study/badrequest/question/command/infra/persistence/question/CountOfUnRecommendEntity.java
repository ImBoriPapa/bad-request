package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfUnRecommend;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class CountOfUnRecommendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long count;

    protected CountOfUnRecommendEntity(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfUnRecommendEntity fromModel(CountOfUnRecommend countOfUnRecommend) {
        return new CountOfUnRecommendEntity(countOfUnRecommend.getId(), countOfUnRecommend.getCount());
    }

    public CountOfUnRecommend toModel() {
        return new CountOfUnRecommend(getId(), getCount());
    }
}
