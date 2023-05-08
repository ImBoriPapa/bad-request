package com.study.badrequest.repository.activity;

import com.study.badrequest.domain.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity,Long> {
}
