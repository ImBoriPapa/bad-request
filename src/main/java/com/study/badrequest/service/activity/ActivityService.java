package com.study.badrequest.service.activity;

import com.study.badrequest.domain.activity.ActivityAction;
import com.study.badrequest.domain.member.Member;

import java.time.LocalDateTime;

public interface ActivityService {

    void createActivity(Member member, String title, ActivityAction action, LocalDateTime createdAt);

}
