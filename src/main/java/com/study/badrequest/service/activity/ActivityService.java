package com.study.badrequest.service.activity;

import com.study.badrequest.active.command.domain.ActivityAction;
import com.study.badrequest.member.command.domain.Member;

import java.time.LocalDateTime;

public interface ActivityService {

    void createActivity(Member member, String title, ActivityAction action, LocalDateTime createdAt);

}
