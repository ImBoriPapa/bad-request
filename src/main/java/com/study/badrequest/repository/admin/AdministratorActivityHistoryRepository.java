package com.study.badrequest.repository.admin;

import com.study.badrequest.domain.admin.AdministratorActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorActivityHistoryRepository extends JpaRepository<AdministratorActivityHistory,Long> {
}
