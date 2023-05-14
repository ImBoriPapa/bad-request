package com.study.badrequest.event.member;


import com.study.badrequest.domain.member.AuthenticationCode;
import com.study.badrequest.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberEventDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Member member;
        private String specialNote;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Update {
        private Member member;
        private String specialNote;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete {

        private Member member;

        private String specialNote;

        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Login {

        private Member member;

        private String specialNote;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Logout {

        private Member member;
        private String specialNote;
        private LocalDateTime recordTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SendAuthenticationMail {
        private AuthenticationCode authenticationCode;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class IssueTemporaryPassword {
        private Member member;
        private String temporaryPassword;
        private String specialNote;
        private LocalDateTime recordTime;

    }
}
