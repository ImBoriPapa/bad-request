package com.study.badrequest.domain.member;


import com.study.badrequest.domain.login.OauthProvider;
import com.study.badrequest.exception.CustomRuntimeException;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "MEMBER", indexes = {
        @Index(name = "MEMBER_EMAIL_IDX", columnList = "EMAIL"),
        @Index(name = "MEMBER_CREATE_DATE_TIME_IDX", columnList = "DATE_INDEX")
})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "CHANGE_ABLE_ID", unique = true, nullable = false)
    private String changeableId;
    @Column(name = "OAUTH_ID")
    private String oauthId;
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MEMBER_PROFILE_ID")
    private MemberProfile memberProfile;
    @Column(name = "IS_OAUTH")
    private Boolean isOauthLogin;
    @Enumerated(EnumType.STRING)
    @Column(name = "OAUTH_PROVIDER")
    private OauthProvider oauthProvider;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "AUTHORITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "IP_ADDRESS")
    private String ipAddress;
    @Column(name = "MEMBER_ACCOUNT_STATUS")
    @Enumerated(EnumType.STRING)
    private MemberAccountStatus accountStatus;
    @Column(name = "ACCEPT_EMAIL")
    private Boolean acceptEmail;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    @Column(name = "TEMPORARY_PASSWORD_ISSUED_AT")
    private LocalDateTime temporaryPasswordIssuedAt;
    @Column(name = "DATE_INDEX")
    private Long createDateTimeIndex;

    @Builder
    public Member(String oauthId, Boolean isOauthLogin, OauthProvider oauthProvider, String email, String password, String contact, Authority authority, String ipAddress, MemberProfile memberProfile, MemberAccountStatus accountStatus, LocalDateTime temporaryPasswordIssuedAt) {
        this.oauthId = oauthId;
        this.isOauthLogin = isOauthLogin;
        this.oauthProvider = oauthProvider;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.memberProfile = memberProfile;
        this.accountStatus = accountStatus;
        this.temporaryPasswordIssuedAt = temporaryPasswordIssuedAt;
        this.acceptEmail = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.createDateTimeIndex = timeToDateIndex(this.createdAt);
    }

    public static Member createSelfRegisteredMember(String email, String password, String contact, MemberProfile memberProfile) {
        return Member.builder()
                .email(email)
                .oauthId(null)
                .password(password)
                .isOauthLogin(false)
                .oauthProvider(OauthProvider.BAD_REQUEST)
                .contact(contact)
                .memberProfile(memberProfile)
                .authority(Authority.MEMBER)
                .accountStatus(MemberAccountStatus.STEADY)
                .build();
    }

    public static Member createOauthMember(String email, String oAutId, OauthProvider oauthProvider, MemberProfile memberProfile) {
        return Member.builder()
                .email(email)
                .oauthId(oAutId)
                .password(null)
                .isOauthLogin(true)
                .oauthProvider(oauthProvider)
                .contact(null)
                .authority(Authority.MEMBER)
                .memberProfile(memberProfile)
                .accountStatus(MemberAccountStatus.STEADY)
                .build();
    }

    private long timeToDateIndex(LocalDateTime localDateTime) {
        return Long.parseLong(localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }

    public void setLastLoginIP(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        }
    }

    public boolean updateOauthMember(String oauthId, String name) {
        if (!this.getMemberProfile().getNickname().equals(name)) {
            this.oauthId = oauthId;
            this.getMemberProfile().changeNickname(name);
            return true;
        }
        return false;
    }

    public void checkConfirmedMail() {
        if (this.accountStatus == MemberAccountStatus.REQUIRED_MAIL_CONFIRMED) {
            throw new CustomRuntimeException(IS_NOT_CONFIRMED_MAIL);
        }
    }

    public void checkTemporaryPassword() {
        if (this.accountStatus == MemberAccountStatus.PASSWORD_IS_TEMPORARY) {
            if (this.temporaryPasswordIssuedAt.plusHours(24).isAfter(LocalDateTime.now())) {
                throw new CustomRuntimeException(IS_EXPIRED_TEMPORARY_PASSWORD);
            }
        }
    }

    public void replacePasswordToTemporaryPassword(String password) {
        this.password = password;
        this.accountStatus = MemberAccountStatus.PASSWORD_IS_TEMPORARY;
        this.temporaryPasswordIssuedAt = LocalDateTime.now();
    }

    @PrePersist
    private void generateChangeableId() {
        this.changeableId = UUID.randomUUID() + "-" + this.createDateTimeIndex;
    }

    public void replaceChangeableId() {
        generateChangeableId();
    }

    public void changePermissions(Authority authority) {
        this.authority = authority;
    }

    public void changePassword(String password) {
        this.password = password;
        this.accountStatus = MemberAccountStatus.STEADY;
    }

    public void changeContact(String contact) {
        this.contact = contact;
    }

    public static Long getCreatedAtInChangeableId(String changeableId) {
        return Long.valueOf(changeableId.split("-")[5]);
    }
}
