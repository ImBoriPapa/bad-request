package com.study.badrequest.domain.member.repository.query;

import com.querydsl.core.types.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberListDto {
    private Long offSet;
    private Integer size;
    private Order order;
    private boolean first;
    private boolean last;
    private Long currentPageNumber;
    private Long totalPages;
    private Integer totalElements;
    private Long totalMembers;
    private List<MemberListResult> results;

    @Builder
    public MemberListDto(Long offSet, Integer size, Order order, boolean first, boolean last, Long currentPageNumber, Integer totalElements, Long totalMembers, Long totalPages, List<MemberListResult> memberListResults) {
        this.offSet = offSet;
        this.size = size;
        this.order = order;
        this.first = first;
        this.last = last;
        this.currentPageNumber = currentPageNumber;
        this.totalElements = totalElements;
        this.totalMembers = totalMembers;
        this.totalPages = totalPages;
        this.results = memberListResults;
    }
}
