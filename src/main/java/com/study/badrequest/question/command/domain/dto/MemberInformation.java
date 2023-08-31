package com.study.badrequest.question.command.domain.dto;

import com.study.badrequest.member.command.domain.values.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberInformation {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private Integer activityScore;
    private Authority authority;
    public MemberInformation(Long memberId, String nickname, String profileImage, Integer activityScore, Authority authority) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
        this.authority = authority;
    }
}
