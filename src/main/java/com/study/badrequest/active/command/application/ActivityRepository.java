package com.study.badrequest.active.command.application;

import com.study.badrequest.active.command.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity,Long> {
}
