package com.study.badrequest.domain.Member.dto;

import com.study.badrequest.domain.Member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
public class MemberResponseForm {

    @NoArgsConstructor
    @Getter
    public static class SignupResult {
        private Long memberId;
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
        private LocalDateTime updatedAt;

        public UpdateResult(Member member) {
            this.memberId = member.getId();
            this.updatedAt = member.getUpdatedAt();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DeleteResult{
        private String thanks = "이용해 주셔서 감사합니다.";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ValidateEmail{
        private boolean duplicate;
        private String email;
    }
}
