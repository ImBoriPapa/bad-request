package com.study.badrequest.domain.record.repository;

import com.study.badrequest.domain.record.entity.MemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecordRepository extends JpaRepository<MemberRecord,Long> {
}
