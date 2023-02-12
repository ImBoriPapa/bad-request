package com.study.badrequest.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
public class MemberResponse {

    @NoArgsConstructor
    @Getter
    public static class SignupResult {
        private Long memberId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public SignupResult(Member member) {
            this.memberId = member.getId();
            this.createdAt = member.getCreatedAt();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateResult {
        private Long memberId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;

        public UpdateResult(Member member) {
            this.memberId = member.getId();
            this.updatedAt = member.getUpdatedAt();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DeleteResult {
        private String thanks = "이용해 주셔서 감사합니다.";
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt = LocalDateTime.now();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ValidateEmailResult {
        private boolean duplicate;
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AuthResult {
        private Long id;
        private Authority authority;
    }
}
