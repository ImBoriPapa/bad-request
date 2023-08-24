package com.study.badrequest.record.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.record.command.domain.MemberRecord;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import com.study.badrequest.record.command.domain.MemberRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final MemberRepository memberRepository;
    private final MemberRecordRepository memberRecordRepository;

    @Transactional
    public void recordMemberInformation(MemberRecordRequest request) {
        log.info("Saving a Member Activity History- action: {},memberId: {}, time: {}", request.getActionStatus(), request.getMemberId(), LocalDateTime.now());

        MemberEntity member = null;

        MemberRecord memberRecord = MemberRecord.builder()
                .action(request.getActionStatus())
                .member(member)
                .ipAddress(request.getIpAddress())
                .description(request.getDescription())
                .recodeTime(request.getRecordTime())
                .build();

        memberRecordRepository.save(memberRecord);

    }
}
