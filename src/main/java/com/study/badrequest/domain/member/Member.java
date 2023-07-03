package com.study.badrequest.domain.member;


import com.study.badrequest.domain.memberProfile.MemberProfile;
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
    @Column(name = "change_able_id", unique = true, nullable = false)
    private String changeableId;
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
    @Column(name = "date_index")
    private Long dateIndex;

    @Builder(access = AccessLevel.PROTECTED)
    protected Member(String oauthId, RegistrationType registrationType, String email, String password, String contact, Authority authority, String ipAddress, AccountStatus accountStatus) {
        this.oauthId = oauthId;
        this.registrationType = registrationType;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.ipAddress = ipAddress;
        this.accountStatus = accountStatus;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
        this.dateIndex = timeToDateIndex(this.createdAt);
    }

    public static Member createMemberWithEmail(String email, String password, String contact) {
        return Member.builder()
                .email(email)
                .oauthId(null)
                .password(password)
                .registrationType(RegistrationType.BAD_REQUEST)
                .contact(contact)
                .authority(Authority.MEMBER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    public static Member createMemberWithOauth(String email, String oauthId, RegistrationType registrationType) {
        return Member.builder()
                .email(email)
                .oauthId(oauthId)
                .password(null)
                .registrationType(registrationType)
                .contact(null)
                .authority(Authority.MEMBER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    public void addMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
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
        this.changeableId = UUID.randomUUID() + "-" + this.dateIndex;
    }

    public void replaceChangeableId() {
        generateChangeableId();
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

    public static Long getDateIndexInChangeableId(String changeableId) {
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
