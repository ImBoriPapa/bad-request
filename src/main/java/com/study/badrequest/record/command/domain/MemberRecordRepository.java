package com.study.badrequest.record.command.domain;


import com.study.badrequest.record.command.domain.MemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecordRepository extends JpaRepository<MemberRecord,Long> {
}
