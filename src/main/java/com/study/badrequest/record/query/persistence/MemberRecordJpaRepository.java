package com.study.badrequest.record.query.persistence;

import com.study.badrequest.record.command.domain.MemberRecord;
import com.study.badrequest.record.command.domain.MemberRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecordJpaRepository extends JpaRepository<MemberRecord,Long>, MemberRecordRepository {
}
