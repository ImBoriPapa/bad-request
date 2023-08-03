package com.study.badrequest.member.command.interfaces;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class MemberProfileResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long id;
        private LocalDateTime createdAt;
    }
}
