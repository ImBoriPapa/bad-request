package com.study.badrequest.event.record;

import com.study.badrequest.domain.record.entity.MemberRecord;
import com.study.badrequest.domain.record.entity.ActionStatus;
import com.study.badrequest.domain.record.repository.MemberRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRecordEventListener {
    private final MemberRecordRepository memberRecordRepository;

    @EventListener
    @Transactional
    public void handleRecordEvent(MemberRecordEventDto.Create dto) {
        memberRecordRepository.save(new MemberRecord(dto.getMemberId(), ActionStatus.CREATED));
    }

    @EventListener
    @Transactional
    public void handleRecordEvent(MemberRecordEventDto.Update dto) {
        memberRecordRepository.save(new MemberRecord(dto.getMemberId(), ActionStatus.UPDATED));
    }

    @EventListener
    @Transactional
    public void handleRecordEvent(MemberRecordEventDto.Delete dto) {
        memberRecordRepository.save(new MemberRecord(dto.getMemberId(), ActionStatus.DELETED));
    }
}
