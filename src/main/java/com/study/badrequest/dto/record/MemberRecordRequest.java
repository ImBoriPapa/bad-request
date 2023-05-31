package com.study.badrequest.dto.record;


import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.record.ActionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRecordRequest {
    private ActionStatus actionStatus;
    private Member member;
    private String ipAddress;
    private String description;
    private LocalDateTime recordTime;
}
