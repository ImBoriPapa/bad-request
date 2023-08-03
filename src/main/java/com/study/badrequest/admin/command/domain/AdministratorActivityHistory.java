package com.study.badrequest.admin.command.domain;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.Authority;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.common.exception.CustomRuntimeException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "ADMINISTRATOR_ACTIVITY_HISTORY")
@EqualsAndHashCode(of = "id")
public class AdministratorActivityHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long id;
    private String activityDetails;
    private String reason;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member admin;
    private LocalDateTime activityAt;

    protected AdministratorActivityHistory(String activityDetails, String reason, Member admin, LocalDateTime activityAt) {
        this.activityDetails = activityDetails;
        this.reason = reason;
        this.admin = admin;
        this.activityAt = activityAt;
    }

    public static AdministratorActivityHistory createAdministratorActivityHistory(String activityDetails, String reason, Member admin, LocalDateTime activityAt) {

        if (admin.getAuthority() != Authority.ADMIN) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.PERMISSION_DENIED);
        }

        return new AdministratorActivityHistory(activityDetails, reason, admin, activityAt);
    }
}
