package com.study.badrequest.member.command.interfaces;

import com.querydsl.core.types.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSearchCondition {

    private Long offset;
    private Integer size;
    private Order order;
}
