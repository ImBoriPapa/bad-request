package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfView;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "view_count_of_question", indexes = {@Index(name = "count_idx", columnList = "count DESC")})
@Getter
public class CountOfViewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long count;

    public CountOfViewEntity(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfViewEntity fromModel(CountOfView countOfView) {
        return new CountOfViewEntity(countOfView.getId(), countOfView.getCount());
    }

    public CountOfView toModel() {
        return CountOfView.initialize(this.getId(), this.getCount());
    }
}
