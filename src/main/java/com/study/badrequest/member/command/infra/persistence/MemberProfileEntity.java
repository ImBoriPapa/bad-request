package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.model.MemberProfile;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_profile")
@EqualsAndHashCode(of = "id")
public class MemberProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "introduce")
    private String introduce;
    @Embedded
    private ProfileImageValue profileImage;
    @Column(name = "activity_score")
    private Integer activityScore;
    @Builder
    public MemberProfileEntity(Long id, String nickname, String introduce, ProfileImageValue profileImage, Integer activityScore) {
        this.id = id;
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
    }

    public static MemberProfileEntity fromModel(MemberProfile memberProfile) {
        return MemberProfileEntity
                .builder()
                .id(memberProfile.getId())
                .nickname(memberProfile.getNickname())
                .introduce(memberProfile.getIntroduce())
                .profileImage(ProfileImageValue.fromModel(memberProfile.getProfileImage()))
                .activityScore(memberProfile.getActivityScore())
                .build();
    }

    public MemberProfile toModel() {
        return new MemberProfile(getId(), getNickname(), getIntroduce(), getProfileImage().toModel(), getActivityScore());
    }
}
