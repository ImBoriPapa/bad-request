package com.study.badrequest.question.command.domain.values;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class MemberId {
    @Column(name = "MEMBER_ID")
    private Long id;
    public MemberId(Long id) {
        this.id = id;
    }

}
