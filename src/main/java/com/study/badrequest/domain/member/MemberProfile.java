package com.study.badrequest.domain.member;


import com.study.badrequest.domain.activity.ActivityScoreEnum;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "MEMBER_PROFILE")
@EqualsAndHashCode(of = "id")
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_PROFILE_ID")
    private Long id;
    @Column(name = "MEMBER_NICKNAME")
    private String nickname;
    @Column(name = "SELF_INTRODUCE")
    private String selfIntroduce;
    @Embedded
    private ProfileImage profileImage;
    @Column(name = "ACTIVITY_SCORE")
    private Integer activityScore;

    public MemberProfile(String nickname, ProfileImage profileImage) {
        this.nickname = nickname;
        this.selfIntroduce = "자기 소개를 입력해 주세요";
        this.profileImage = profileImage;
        this.activityScore = 10;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public void incrementActivityScore(int score) {
        this.activityScore = this.activityScore + score;
    }
}
