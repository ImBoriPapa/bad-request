package com.study.badrequest.domain.log.repositoey;

import com.study.badrequest.domain.log.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity,Long> {
}
