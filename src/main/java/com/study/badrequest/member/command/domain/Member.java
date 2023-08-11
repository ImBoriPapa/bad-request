package com.study.badrequest.member.command.domain;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.study.badrequest.common.response.ApiResponseStatus.NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT;
import static com.study.badrequest.common.response.ApiResponseStatus.WRONG_PASSWORD;
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
    @Embedded
    private MemberEmail email;
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
    @Column(name = "withdrawal_at")
    private LocalDateTime withdrawalAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected Member(String oauthId, RegistrationType registrationType, MemberEmail email, String password, String contact, Authority authority, String ipAddress, AccountStatus accountStatus, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime withdrawalAt) {
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
        this.withdrawalAt = withdrawalAt;
    }

    /**
     * 회원 Entity 생성: 이메일로 가입시 회원 정보 생성.
     *
     * @param email                 : 회원 이메일 (String, required)
     * @param password              : 비밀번호 (String, required)
     * @param nickname              : 닉네임 (String, required)
     * @param contact               : 연락처 (String, required)
     * @param imageLocation         : 이미지 경로 (String, required)
     * @param memberPasswordEncoder : 비밀번호 인코더 (String, MemberPasswordEncoder)
     * @return Member
     * @ImplNote 이 메서드는 지정된 정보로 새 멤버 엔티티를 생성하여 DDD 원칙을 따릅니다.
     * 또한 제공된 별명과 기본 프로필 이미지로 구성원 프로필을 작성합니다.
     * 암호는 제공된 MemberPasswordEncoder 사용하여 암호화됩니다.
     * 계정은 기본적으로 ACTIVE 상태로 설정됩니다.
     * 이 메서드는 이메일의 고유성 검사를 처리하지 않으므로 호출하기 전에 고유성을 확인햐야 합니다.
     * @see MemberProfile#createMemberProfile(String, ProfileImage)
     * @see ProfileImage#createDefaultImage(String)
     * @see Member#assignMemberProfile(MemberProfile)
     * @see Member#generateAuthenticationCode()
     */
    public static Member createByEmail(String email, String password, String nickname, String contact, String imageLocation, MemberPasswordEncoder memberPasswordEncoder) {

        final MemberProfile memberProfile = MemberProfile.createMemberProfile(nickname, ProfileImage.createDefaultImage(imageLocation));
        final MemberEmail memberEmail = MemberEmail.createMemberEmail(email);

        Member member = Member.builder()
                .email(memberEmail)
                .oauthId(null)
                .password(memberPasswordEncoder.encode(password))
                .registrationType(BAD_REQUEST)
                .contact(contact)
                .authority(MEMBER)
                .accountStatus(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .withdrawalAt(null)
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
                .email(new MemberEmail(email))
                .oauthId(oauthId)
                .password(null)
                .registrationType(registrationType)
                .contact(null)
                .authority(MEMBER)
                .accountStatus(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .withdrawalAt(null)
                .build();

        member.generateAuthenticationCode();

        return member;
    }

    public boolean isActive() {
        return this.accountStatus != WITHDRAWN;
    }

    public void withdrawn() {
        this.withdrawalAt = LocalDateTime.now();
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

    public void changePassword(String currentPassword, String newPassword, MemberPasswordEncoder memberPasswordEncoder) {

        if (currentPassword.equals(newPassword)) {
            throw CustomRuntimeException.createWithApiResponseStatus(NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        if (!memberPasswordEncoder.matches(newPassword, this.getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.WRONG_PASSWORD);
        }

        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
        this.accountStatus = ACTIVE;
    }

    public void changeContact(String contact) {
        this.contact = contact;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeToWithDrawn(String password,MemberPasswordEncoder memberPasswordEncoder) {

        if(!memberPasswordEncoder.matches(password,this.getPassword())){
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_PASSWORD);
        }

        this.accountStatus = WITHDRAWN;
        this.withdrawalAt = LocalDateTime.now();
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
