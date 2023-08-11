package com.study.badrequest.active.command.infra.persistence;

import com.study.badrequest.active.command.domain.Activity;
import com.study.badrequest.active.command.domain.ActivityRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityJpaRepository extends JpaRepository<Activity,Long>, ActivityRepository {
}
