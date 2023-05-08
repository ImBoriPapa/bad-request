package com.study.badrequest.service.activity;

import com.study.badrequest.domain.activity.Activity;
import com.study.badrequest.domain.activity.ActivityScoreEnum;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.repository.activity.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityServiceImpl {
    private final ActivityRepository activityRepository;

    @Transactional
    public void createActivity(Member member, String title, Long questionId, LocalDateTime createdAt) {
        log.info("활동 내용 저장");

        Activity activity = Activity.postQuestion(member, title, "질문을 작성했습니다.", questionId, createdAt);

        Activity save = activityRepository.save(activity);
        save.getMember().getMemberProfile().incrementActivityScore(ActivityScoreEnum.WRITE_QUESTION);
    }

}
