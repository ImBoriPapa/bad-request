package com.study.badrequest.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
public class MemberResponse {

    @NoArgsConstructor
    @Getter
    public static class Create {
        private Long memberId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public Create(Member member) {
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


    @Getter
    public static class DeleteResult {
        private Map<String, String> thanks = new HashMap<>();
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt;

        public DeleteResult() {
            this.thanks.put("thanks", "그동안 감사했습니다.");
            this.deletedAt = LocalDateTime.now();
        }
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
