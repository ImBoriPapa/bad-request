package com.study.badrequest.service.activity;

import com.study.badrequest.domain.activity.Activity;
import com.study.badrequest.domain.activity.ActivityAction;
import com.study.badrequest.domain.activity.ActivityScoreEnum;
import com.study.badrequest.domain.member.Member;
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
    public void createQuestionActivity(Member member, String title, LocalDateTime createdAt) {
        log.info("활동 내용 저장");

        Activity activity = Activity.createActivity(member, ActivityAction.QUESTION, title, "질문을 등록 했습니다.", createdAt);

        Activity save = activityRepository.save(activity);

        save.getMember().incrementActivityScore(ActivityScoreEnum.WRITE_QUESTION);
    }

    @Async(ACTIVITY_ASYNC_EXECUTOR)
    @Transactional
    public void createAnswerActivity(Member member, String simpleContents, LocalDateTime createdAt) {
        log.info("활동 내용 저장");
        Activity activity = Activity.createActivity(member, ActivityAction.ANSWER, simpleContents, "답변을 등록 했습니다.", createdAt);

        Activity save = activityRepository.save(activity);

        save.getMember().incrementActivityScore(ActivityScoreEnum.WRITE_ANSWER);

    }

}
