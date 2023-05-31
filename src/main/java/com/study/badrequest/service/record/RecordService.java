package com.study.badrequest.service.record;

import com.study.badrequest.dto.record.MemberRecordRequest;

public interface RecordService {
    void recordMemberInformation(MemberRecordRequest request);
}
