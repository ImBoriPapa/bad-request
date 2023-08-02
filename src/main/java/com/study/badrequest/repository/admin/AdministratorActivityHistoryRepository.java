package com.study.badrequest.repository.admin;

import com.study.badrequest.admin.command.domain.AdministratorActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorActivityHistoryRepository extends JpaRepository<AdministratorActivityHistory,Long> {
}
