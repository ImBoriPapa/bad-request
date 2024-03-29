package com.study.badrequest.member.command.interfaces;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
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
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public Create(Long memberId, LocalDateTime createdAt) {
            this.id = memberId;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor

    @Getter
    public static class SendAuthenticationEmail {
        private String email;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime startedAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime expiredAt;

        public SendAuthenticationEmail(String email, LocalDateTime createdAt, LocalDateTime expiredAt) {
            this.email = email;
            this.startedAt = createdAt;
            this.expiredAt = expiredAt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class EmailConfirm {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime confirmedAt;

        public EmailConfirm(MemberEntity member) {
            this.id = member.getId();
            this.confirmedAt = member.getUpdatedAt();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Update {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;

        public Update(MemberEntity member) {
            this.id = member.getId();
            this.updatedAt = member.getUpdatedAt();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TemporaryPassword {

        private String email;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime issuedAt;

        public TemporaryPassword(String email, LocalDateTime issuedAt) {
            this.email = email;
            this.issuedAt = issuedAt;
        }
    }

    @Getter
    public static class Delete {
        private Map<String, String> thanks = new HashMap<>();
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt;

        public Delete() {
            this.thanks.put("thanks", "그동안 감사했습니다.");
            this.deletedAt = LocalDateTime.now();
        }
    }

}
