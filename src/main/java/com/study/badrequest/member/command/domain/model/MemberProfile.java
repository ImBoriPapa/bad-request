package com.study.badrequest.member.command.domain.model;

import lombok.Getter;

@Getter
public class MemberProfile {
    private final Long id;
    private final String nickname;
    private final String introduce;
    private final ProfileImage profileImage;
    private final Integer activityScore;

    public MemberProfile(Long id, String nickname, String introduce, ProfileImage profileImage, Integer activityScore) {
        this.id = id;
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
    }

    public static MemberProfile createMemberProfile(String nickname, ProfileImage profileImage) {
        final String DEFAULT_INTRODUCE = "자기 소개를 입력해 주세요";
        final int DEFAULT_SCORE = 10;
        return new MemberProfile(0L,nickname, DEFAULT_INTRODUCE, profileImage, DEFAULT_SCORE);
    }
}
