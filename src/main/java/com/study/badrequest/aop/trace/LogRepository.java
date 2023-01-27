package com.study.badrequest.aop.trace;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity,Long> {
}
