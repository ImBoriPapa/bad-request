package com.study.badrequest.member.command.domain;


import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.study.badrequest.member.command.domain.AccountStatus.*;
import static com.study.badrequest.member.command.domain.Authority.*;
import static com.study.badrequest.member.command.domain.RegistrationType.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "member", indexes = {
        @Index(name = "MEMBER_EMAIL_IDX", columnList = "email"),
        @Index(name = "MEMBER_CONTACT_IDX", columnList = "contact"),
        @Index(name = "MEMBER_CREATE_DATE_TIME_IDX", columnList = "created_at")
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

    /**
     * 회원 Entity 생성: 이메일로 가입시 회원 정보 생성
     *
     * @param email         : 회원이 사용하는 이메일
     * @param password      : 비밀번호
     * @param contact       : 연락처
     * @param memberProfile : 회원 프로필 entity
     * @return Member
     */
    public static Member createByEmail(String email, String password, String contact, MemberProfile memberProfile) {
        assert email != null;
        Member member = Member.builder()
                .email(email)
                .oauthId(null)
                .password(password)
                .registrationType(BAD_REQUEST)
                .contact(contact)
                .authority(MEMBER)
                .accountStatus(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        member.assignMemberProfile(memberProfile);
        member.generateAuthenticationCode();

        return member;
    }

    /**
     * 회원 Entity 생성: OAuth 로 회원 정보 생성
     *
     * @param email            : email
     * @param oauthId          : oauthId
     * @param registrationType : registrationType
     * @return Member
     */
    public static Member createByOAuth2(String email, String oauthId, RegistrationType registrationType) {

        Member member = Member.builder()
                .email(email)
                .oauthId(oauthId)
                .password(null)
                .registrationType(registrationType)
                .contact(null)
                .authority(MEMBER)
                .accountStatus(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        member.generateAuthenticationCode();

        return member;
    }

    public boolean isActive() {
        return this.accountStatus != WITHDRAWN;
    }

    public void withdrawn() {
        this.deletedAt = LocalDateTime.now();
        this.changeStatus(WITHDRAWN);
    }

    public void useTemporaryPassword() {
        this.updatedAt = LocalDateTime.now();
        this.changeStatus(USING_TEMPORARY_PASSWORD);
    }

    public void useNotConfirmed() {
        this.updatedAt = LocalDateTime.now();
        this.changeStatus(USING_NOT_CONFIRMED_EMAIL);
    }

    private void assignMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    private void changeStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignIpAddress(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        }
    }

    private void generateAuthenticationCode() {
        this.authenticationCode = UUID.randomUUID() + "/" + createdAt.toString();
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
        this.accountStatus = WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public static LocalDateTime getCreatedAtInAuthenticationCode(String authenticationCode) {
        String stringCreatedAt = authenticationCode.split("/")[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(stringCreatedAt, formatter);
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
