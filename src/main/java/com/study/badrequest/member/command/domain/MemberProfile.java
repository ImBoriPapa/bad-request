package com.study.badrequest.member.command.domain;


import com.study.badrequest.active.command.domain.ActivityScore;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_profile")
@EqualsAndHashCode(of = "id")
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "introduce")
    private String introduce;
    @Embedded
    private ProfileImage profileImage;
    @Column(name = "activity_score")
    private Integer activityScore;

    protected MemberProfile(String nickname, String introduce, ProfileImage profileImage, Integer activityScore) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
    }

    public static MemberProfile createMemberProfile(String nickname, ProfileImage profileImage) {
        final String DEFAULT_INTRODUCE = "자기 소개를 입력해 주세요";
        final int DEFAULT_SCORE = 10;
        return new MemberProfile(nickname, DEFAULT_INTRODUCE, profileImage, DEFAULT_SCORE);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeIntroduce(String selfIntroduce) {
        this.introduce = selfIntroduce;
    }

    public void incrementActivityScore(ActivityScore score) {
        this.activityScore = this.activityScore + score.getScore();
    }

    protected void replaceProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }
}
