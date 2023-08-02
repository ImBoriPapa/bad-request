package com.study.badrequest.event.member;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberEventDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Long memberId;
        private String nickname;
        private String description;
        private String ipAddress;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Update {
        private Long memberId;
        private String description;
        private String ipAddress;
        private LocalDateTime recordTime;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete {

        private Long memberId;
        private String description;

        private String ipAddress;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Login {
        private Long memberId;
        private String description;
        private String ipAddress;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Logout {
        private Long memberId;
        private String description;
        private String ipAddress;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SendAuthenticationMail {
        private String email;
        private String code;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class IssueTemporaryPassword {
        private Long memberId;
        private String temporaryPassword;
        private String description;
        private String ipAddress;
        private LocalDateTime recordTime;

    }
}
