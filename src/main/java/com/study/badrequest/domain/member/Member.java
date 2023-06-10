package com.study.badrequest.domain.member;


import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "MEMBER", indexes = {
        @Index(name = "MEMBER_EMAIL_IDX", columnList = "EMAIL"),
        @Index(name = "MEMBER_CONTACT_IDX", columnList = "CONTACT"),
        @Index(name = "MEMBER_CREATE_DATE_TIME_IDX", columnList = "DATE_INDEX")
})
public class Member {
    // TODO: 2023/06/03 탈퇴 처리 계획 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "CHANGE_ABLE_ID", unique = true, nullable = false)
    private String changeableId;
    @Column(name = "OAUTH_ID")
    private String oauthId;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_PROFILE_ID")
    private MemberProfile memberProfile;
    @Enumerated(EnumType.STRING)
    @Column(name = "REGISTRATION_TYPE")
    private RegistrationType registrationType;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "AUTHORITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "IP_ADDRESS")
    private String ipAddress;
    @Column(name = "ACCOUNT_STATUS")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
    @Column(name = "DATE_INDEX")
    private Long createDateTimeIndex;

    @Builder(access = AccessLevel.PROTECTED)
    protected Member(String oauthId, RegistrationType registrationType, String email, String password, String contact, Authority authority, String ipAddress, MemberProfile memberProfile, AccountStatus accountStatus) {
        this.oauthId = oauthId;
        this.registrationType = registrationType;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.memberProfile = memberProfile;
        this.accountStatus = accountStatus;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
        this.createDateTimeIndex = timeToDateIndex(this.createdAt);
    }

    public static Member createMemberWithEmail(String email, String password, String contact, MemberProfile memberProfile) {
        return Member.builder()
                .email(email)
                .oauthId(null)
                .password(password)
                .registrationType(RegistrationType.BAD_REQUEST)
                .contact(contact)
                .memberProfile(memberProfile)
                .authority(Authority.MEMBER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    public static Member createMemberWithOauth(String email, String oauthId, RegistrationType registrationType, MemberProfile memberProfile) {
        return Member.builder()
                .email(email)
                .oauthId(oauthId)
                .password(null)
                .registrationType(registrationType)
                .contact(null)
                .authority(Authority.MEMBER)
                .memberProfile(memberProfile)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    public void changeStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.updatedAt = LocalDateTime.now();
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
        this.accountStatus = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeContact(String contact) {
        this.contact = contact;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeToWithDrawn() {
        this.accountStatus = AccountStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public static Long getCreatedAtInChangeableId(String changeableId) {
        return Long.valueOf(changeableId.split("-")[5]);
    }

    public void changeNickname(String nickname) {
        this.memberProfile.changeNickname(nickname);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeIntroduce(String introduce) {
        this.memberProfile.changeIntroduce(introduce);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeProfileImageToDefault(String imageLocation) {
        this.memberProfile.getProfileImage().replaceDefaultImage(imageLocation);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeProfileImage(String storeFilName, String imageLocation, Long size) {
        this.memberProfile.getProfileImage().replaceProfileImage(storeFilName, imageLocation, size);
        this.updatedAt = LocalDateTime.now();
    }

}
