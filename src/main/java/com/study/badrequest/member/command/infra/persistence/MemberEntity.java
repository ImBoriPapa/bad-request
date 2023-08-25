package com.study.badrequest.member.command.infra.persistence;


import com.study.badrequest.member.command.domain.dto.MemberInitialize;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.model.MemberPassword;
import com.study.badrequest.member.command.domain.values.AccountStatus;
import com.study.badrequest.member.command.domain.values.Authority;
import com.study.badrequest.member.command.domain.values.PasswordType;
import com.study.badrequest.member.command.domain.values.RegistrationType;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "member", indexes = {
        @Index(name = "MEMBER_EMAIL_IDX", columnList = "email"),
        @Index(name = "MEMBER_CONTACT_IDX", columnList = "contact")
})
public class MemberEntity {
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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_profile_id")
    private MemberProfileEntity memberProfile;
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type")
    private RegistrationType registrationType;
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    private PasswordType passwordType;
    @Column(name = "password_created_at")
    private LocalDateTime passwordCreatedAt;
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
    @Column(name = "sign_in_at")
    private LocalDateTime signInAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "resign_at")
    private LocalDateTime resignAt;

    @Builder(access = AccessLevel.PROTECTED)
    public MemberEntity(Long id, String authenticationCode, String oauthId, String email, MemberProfileEntity memberProfile, RegistrationType registrationType, String password, PasswordType passwordType, LocalDateTime passwordCreatedAt, String contact, Authority authority, String ipAddress, AccountStatus accountStatus, LocalDateTime signInAt, LocalDateTime updatedAt, LocalDateTime resignAt) {
        this.id = id;
        this.authenticationCode = authenticationCode;
        this.oauthId = oauthId;
        this.email = email;
        this.memberProfile = memberProfile;
        this.registrationType = registrationType;
        this.password = password;
        this.passwordType = passwordType;
        this.passwordCreatedAt = passwordCreatedAt;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.accountStatus = accountStatus;
        this.signInAt = signInAt;
        this.updatedAt = updatedAt;
        this.resignAt = resignAt;
    }

    public static MemberEntity fromModel(Member member) {
        return MemberEntity.builder()
                .id(member.getMemberId().getId())
                .authenticationCode(member.getAuthenticationCode())
                .oauthId(member.getOauthId())
                .email(member.getMemberEmail().getEmail())
                .memberProfile(MemberProfileEntity.fromModel(member.getMemberProfile()))
                .registrationType(member.getRegistrationType())
                .password(member.getMemberPassword().getPassword())
                .passwordType(member.getMemberPassword().getPasswordType())
                .passwordCreatedAt(member.getMemberPassword().getCreatedAt())
                .contact(member.getContact())
                .authority(member.getAuthority())
                .accountStatus(member.getAccountStatus())
                .signInAt(member.getSignInAt())
                .updatedAt(member.getUpdatedAt())
                .resignAt(member.getResignAt())
                .build();
    }

    public Member toModel() {
        MemberInitialize memberInitialize = MemberInitialize.builder()
                .memberId(getId())
                .authenticationCode(getAuthenticationCode())
                .oauthId(getOauthId())
                .memberEmail(getEmail())
                .memberProfile(getMemberProfile().toModel())
                .registrationType(getRegistrationType())
                .memberPassword(new MemberPassword(getPassword(), getPasswordType(), getPasswordCreatedAt()))
                .contact(getContact())
                .authority(getAuthority())
                .accountStatus(getAccountStatus())
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(getResignAt())
                .build();
        return Member.initialize(memberInitialize);
    }


    public void assignIpAddress(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        }
    }

    public void changePermissions(Authority authority) {
        this.authority = authority;
        this.updatedAt = LocalDateTime.now();
    }

    public static LocalDateTime getCreatedAtInAuthenticationCode(String authenticationCode) {
        String stringCreatedAt = authenticationCode.split("/")[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(stringCreatedAt, formatter);
    }
}
