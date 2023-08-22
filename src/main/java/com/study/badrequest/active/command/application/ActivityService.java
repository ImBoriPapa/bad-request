package com.study.badrequest.active.command.application;

import com.study.badrequest.active.command.domain.ActivityAction;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;

import java.time.LocalDateTime;

public interface ActivityService {

    void createActivity(MemberEntity member, String title, ActivityAction action, LocalDateTime createdAt);

}
