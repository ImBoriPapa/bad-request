package com.study.badrequest.domain.log.repositoey;

import com.study.badrequest.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log,Long> {
}
