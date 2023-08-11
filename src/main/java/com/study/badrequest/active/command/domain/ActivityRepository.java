package com.study.badrequest.active.command.domain;

import com.study.badrequest.active.command.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository  {

    Activity save(Activity activity);
}
