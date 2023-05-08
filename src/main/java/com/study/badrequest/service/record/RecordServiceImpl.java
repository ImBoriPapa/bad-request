package com.study.badrequest.service.record;

import com.study.badrequest.domain.record.MemberRecord;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.repository.record.MemberRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl {
    private final MemberRecordRepository memberRecordRepository;
    @Transactional
    public void recordMemberInformation(MemberRecordRequest request) {
        log.info("회원 기록 저장 action: {},memberId: {}, time: {}",request.getActionStatus(), request.getMemberId(), LocalDateTime.now());

        MemberRecord memberRecord = MemberRecord.builder()
                .action(request.getActionStatus())
                .memberId(request.getMemberId())
                .memberEmail(request.getMemberEmail())
                .memberAuthority(request.getMemberAuthority())
                .ipAddress(request.getIpAddress())
                .specialNote(request.getSpecialNote())
                .recodeTime(request.getRecordTime())
                .build();

        memberRecordRepository.save(memberRecord);
    }

    public void getRecord(Long memberId) {

    }
}
