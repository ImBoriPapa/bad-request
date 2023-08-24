package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.active.command.domain.ActivityAction;
import lombok.Getter;

@Getter
public final class MemberProfile {
    private final Long id;
    private final String nickname;
    private final String introduce;
    private final ProfileImage profileImage;
    private final Integer activityScore;

    private MemberProfile(Long id, String nickname, String introduce, ProfileImage profileImage, Integer activityScore) {
        this.id = id;
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
    }

    public static MemberProfile createMemberProfile(String nickname, ProfileImage profileImage) {
        final String DEFAULT_INTRODUCE = "자기 소개를 입력해 주세요";
        final int DEFAULT_SCORE = 10;
        return new MemberProfile(null, nickname, DEFAULT_INTRODUCE, profileImage, DEFAULT_SCORE);
    }

    public static MemberProfile initialize(Long id, String nickname, String introduce, ProfileImage profileImage, Integer activityScore) {
        return new MemberProfile(id, nickname, introduce, profileImage, activityScore);
    }

    public MemberProfile increaseActiveScore(ActivityAction activityAction) {
        return new MemberProfile(getId(), getNickname(), getIntroduce(), getProfileImage(), getActivityScore() + activityAction.getScore());
    }
}
