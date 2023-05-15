package com.study.badrequest.service.activity;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.activity.Activity;
import com.study.badrequest.domain.activity.ActivityScoreEnum;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.activity.ActivityRepository;
import com.study.badrequest.repository.member.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;


import static com.study.badrequest.config.AsyncConfig.ACTIVITY_ASYNC_EXECUTOR;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityServiceImpl {
    private final ActivityRepository activityRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Async(ACTIVITY_ASYNC_EXECUTOR)
    @Transactional
    public void createActivity(Member member, String title, LocalDateTime createdAt) {
        log.info("활동 내용 저장");

        Activity activity = Activity.postQuestion(member, title, "질문을 작성했습니다.", createdAt);

        activityRepository.save(activity);

        Optional<MemberProfile> optional = memberProfileRepository.findById(activity.getMember().getMemberProfile().getId());

        optional.ifPresent(memberProfile -> memberProfile.incrementActivityScore(ActivityScoreEnum.WRITE_QUESTION));

    }

}
