package com.study.badrequest.domain.member.repository.query;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberDetailDto {

    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String contact;

    private String profileImagePath;

    private Authority authority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
