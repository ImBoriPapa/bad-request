package com.study.badrequest.service.activity;

import com.study.badrequest.active.command.domain.Activity;
import com.study.badrequest.active.command.domain.ActivityAction;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.repository.activity.ActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;



import static com.study.badrequest.config.AsyncConfig.ACTIVITY_ASYNC_EXECUTOR;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;

    @Async(ACTIVITY_ASYNC_EXECUTOR)
    @Transactional
    public void createActivity(Member member, String title, ActivityAction action , LocalDateTime createdAt) {
        log.info("활동 내용 저장");

        Activity activity = Activity.createActivity(member, action, title, "질문을 등록 했습니다.", createdAt);

        Activity save = activityRepository.save(activity);

    }



}
