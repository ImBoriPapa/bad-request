package com.study.badrequest.outbox.command.infra.persistence;

import com.study.badrequest.outbox.command.domain.OutBoxMessage;
import com.study.badrequest.outbox.command.domain.OutBoxRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutBoxMessageJpaRepository extends JpaRepository<OutBoxMessage, Long>, OutBoxRepository {
}
