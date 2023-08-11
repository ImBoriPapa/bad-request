package com.study.badrequest.admin.command.infra.persistence;

import com.study.badrequest.admin.command.domain.AdministratorActivityHistory;
import com.study.badrequest.admin.command.domain.AdministratorActivityHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorActivityHistoryJpaRepository extends JpaRepository<AdministratorActivityHistory,Long>, AdministratorActivityHistoryRepository {
}
