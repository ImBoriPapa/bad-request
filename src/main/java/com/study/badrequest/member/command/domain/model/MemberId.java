package com.study.badrequest.member.command.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class MemberId {
    private Long id;

    public MemberId(Long id) {
        this.id = id;
    }
}
