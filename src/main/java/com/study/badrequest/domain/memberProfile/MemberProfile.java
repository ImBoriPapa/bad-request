package com.study.badrequest.domain.memberProfile;


import com.study.badrequest.domain.activity.ActivityScore;
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
    @Column(name = "member_profile_id")
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
        final String introduce = "자기 소개를 입력해 주세요";
        final int score = 10;
        return new MemberProfile(nickname, introduce, profileImage, score);
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
}
