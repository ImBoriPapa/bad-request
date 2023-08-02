package com.study.badrequest.member.command.domain;


import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "member", indexes = {
        @Index(name = "MEMBER_EMAIL_IDX", columnList = "email"),
        @Index(name = "MEMBER_CONTACT_IDX", columnList = "contact"),
        @Index(name = "MEMBER_CREATE_DATE_TIME_IDX", columnList = "date_index")
})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(name = "authentication_code", unique = true, nullable = false)
    private String authenticationCode;
    @Column(name = "oauth_id", nullable = true)
    private String oauthId;
    @Column(name = "email", nullable = false)
    private String email;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type")
    private RegistrationType registrationType;
    @Column(name = "password")
    private String password;
    @Column(name = "contact")
    private String contact;
    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(name = "date_index")
    private Long dateIndex;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected Member(String oauthId, RegistrationType registrationType, String email, String password, String contact, Authority authority, String ipAddress, AccountStatus accountStatus, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.oauthId = oauthId;
        this.registrationType = registrationType;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Member createWithEmail(String email, String password, String contact) {

        Member member = Member.builder()
                .email(email)
                .oauthId(null)
                .password(password)
                .registrationType(RegistrationType.BAD_REQUEST)
                .contact(contact)
                .authority(Authority.MEMBER)
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        member.generateDateTimeIndex();
        member.generateAuthenticationCode();

        return member;
    }

    public static Member createWithOauth2(String email, String oauthId, RegistrationType registrationType) {

        Member member = Member.builder()
                .email(email)
                .oauthId(oauthId)
                .password(null)
                .registrationType(registrationType)
                .contact(null)
                .authority(Authority.MEMBER)
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        member.generateDateTimeIndex();
        member.generateAuthenticationCode();

        return member;
    }

    public void withdrawn() {
        this.deletedAt = LocalDateTime.now();
        this.changeStatus(AccountStatus.WITHDRAWN);
    }

    public void useTemporaryPassword() {
        this.updatedAt = LocalDateTime.now();
        this.changeStatus(AccountStatus.USING_TEMPORARY_PASSWORD);
    }

    public void useNotConfirmed() {
        this.updatedAt = LocalDateTime.now();
        this.changeStatus(AccountStatus.USING_NOT_CONFIRMED_EMAIL);
    }

    public void assignMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    private void changeStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private void generateDateTimeIndex() {
        this.dateIndex = Long.parseLong(this.createdAt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }

    public void assignIpAddress(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        }
    }

    private void generateAuthenticationCode() {
        this.authenticationCode = UUID.randomUUID() + "-" + this.dateIndex;
    }

    public void replaceAuthenticationCode() {
        generateAuthenticationCode();
    }

    public void changePermissions(Authority authority) {
        this.authority = authority;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String password, AccountStatus status) {
        this.password = password;
        this.accountStatus = status;
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

    public static Long getDateIndexInAuthenticationCode(String authenticationCode) {
        return Long.valueOf(authenticationCode.split("-")[5]);
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
