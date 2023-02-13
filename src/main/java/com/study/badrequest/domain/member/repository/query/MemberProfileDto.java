package com.study.badrequest.domain.member.repository.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberProfileDto {
    private Long memberId;
    private String nickname;
    private String aboutMe;
    private String profileImagePath;
}
