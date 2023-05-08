package com.study.badrequest.domain.member;


import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.OauthProvider;
import com.study.badrequest.domain.record.DefaultTime;
import com.study.badrequest.exception.custom_exception.MemberException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.response.ApiResponseStatus.WRONG_EMAIL_PATTERN;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "MEMBER", indexes = {
        @Index(name = "MEMBER_AUTHORITY_IDX", columnList = "AUTHORITY"),
        @Index(name = "MEMBER_DOMAIN_IDX", columnList = "DOMAIN_NAME")
})
public class Member extends DefaultTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "OAUTH_ID")
    private String oauthId;
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MEMBER_PROFILE_ID")
    private MemberProfile memberProfile;
    @Column(name = "IS_OAUTH")
    private Boolean isOauthLogin;
    @Enumerated(EnumType.STRING)
    @Column(name = "OAUTH_PROVIDER")
    private OauthProvider oauthProvider;
    @Column(name = "DOMAIN_NAME")
    private String domainName;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "AUTHORITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
    // TODO: 2023/04/20  IP 주소가 변경될 때 고려사항 정리하기
    @Column(name = "IP_ADDRESS")
    private String ipAddress;
    @Column(name = "MEMBER_ACCOUNT_STATUS")
    @Enumerated(EnumType.STRING)
    private MemberAccountStatus accountStatus;
    @Column(name = "TEMPORARY_PASSWORD_ISSUED_AT")
    private LocalDateTime temporaryPasswordIssuedAt;
    @Column(name = "ACCEPT_EMAIL")
    private Boolean acceptEmail;

    @Builder
    public Member(String oauthId, Boolean isOauthLogin, OauthProvider oauthProvider, String email, String password, String contact, Authority authority, String ipAddress, MemberProfile memberProfile, MemberAccountStatus accountStatus, LocalDateTime temporaryPasswordIssuedAt) {
        this.oauthId = oauthId;
        this.isOauthLogin = isOauthLogin;
        this.oauthProvider = oauthProvider;
        this.email = email;
        this.domainName = extractDomainFromEmail(email);
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.memberProfile = memberProfile;
        this.accountStatus = accountStatus;
        this.temporaryPasswordIssuedAt = temporaryPasswordIssuedAt;
        this.acceptEmail = true;
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

    public void setLastLoginIP(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean updateOauthMember(String oauthId, String name) {
        if (!this.getMemberProfile().getNickname().equals(name)) {
            this.oauthId = oauthId;
            this.getMemberProfile().changeNickname(name);
            return true;
        }
        return false;
    }

    public static String extractDomainFromEmail(String email) {

        String[] parts = email.split("@");

        if (parts.length != 2) {
            throw new MemberException(WRONG_EMAIL_PATTERN);
        }
        String[] domainParts = parts[1].split("\\.");

        if (domainParts.length < 2) {
            throw new MemberException(WRONG_EMAIL_PATTERN);
        }
        return domainParts[0];
    }

    public void checkConfirmedMail() {
        if (this.accountStatus == MemberAccountStatus.REQUIRED_MAIL_CONFIRMED) {
            throw new MemberException(ApiResponseStatus.IS_NOT_CONFIRMED_MAIL);
        }
    }

    public void checkTemporaryPassword() {
        if (this.accountStatus == MemberAccountStatus.PASSWORD_IS_TEMPORARY) {
            if (this.temporaryPasswordIssuedAt.plusHours(24).isAfter(LocalDateTime.now())) {
                throw new MemberException(ApiResponseStatus.IS_EXPIRED_TEMPORARY_PASSWORD);
            }
        }
    }

    public void replacePasswordToTemporaryPassword(String password) {
        this.password = password;
        this.accountStatus = MemberAccountStatus.PASSWORD_IS_TEMPORARY;
        this.temporaryPasswordIssuedAt = LocalDateTime.now();
    }

    @PrePersist
    private void generateUsernameWithUUID() {
        this.username = UUID.randomUUID().toString();
    }

    public void replaceUsername() {
        generateUsernameWithUUID();
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

}
