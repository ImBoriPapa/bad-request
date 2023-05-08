package com.study.badrequest.repository.member.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileDto {
    private Long memberId;
    private String nickname;
    private String selfIntroduce;
    private String profileImage;

}
