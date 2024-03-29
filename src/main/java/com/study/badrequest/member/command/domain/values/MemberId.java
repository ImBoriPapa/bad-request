package com.study.badrequest.member.command.domain.values;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class MemberId {
    private final Long id;

    public MemberId(Long id) {
        this.id = id;
    }
}
