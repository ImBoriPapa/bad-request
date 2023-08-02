package com.study.badrequest.record.command.application;

import com.study.badrequest.dto.record.MemberRecordRequest;

public interface RecordService {
    void recordMemberInformation(MemberRecordRequest request);
}
