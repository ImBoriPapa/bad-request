package com.study.badrequest.record.command.application;


import com.study.badrequest.record.command.domain.ActionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRecordRequest {
    private ActionStatus actionStatus;
    private Long memberId;
    private String ipAddress;
    private String description;
    private LocalDateTime recordTime;
}
