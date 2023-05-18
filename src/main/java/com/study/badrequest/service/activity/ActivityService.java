package com.study.badrequest.service.activity;

import com.study.badrequest.domain.member.Member;

import java.time.LocalDateTime;

public interface ActivityService {

    void createQuestionActivity(Member member, String title, LocalDateTime createdAt);
    void createAnswerActivity(Member member, String simpleContents, LocalDateTime createdAt);
}
