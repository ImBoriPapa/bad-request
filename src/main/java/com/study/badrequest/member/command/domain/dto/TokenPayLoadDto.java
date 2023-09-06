package com.study.badrequest.member.command.domain.dto;

import com.study.badrequest.member.command.domain.values.Authority;

public record TokenPayLoadDto(Long memberId, Authority authority,Boolean isLogin) {
}
