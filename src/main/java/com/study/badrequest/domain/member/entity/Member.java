package com.study.badrequest.domain.member.entity;

import com.study.badrequest.commons.exception.custom_exception.MemberException;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.consts.CustomStatus.WRONG_EMAIL_PATTERN;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "MEMBER", indexes = {
        @Index(name = "MEMBER_AUTHORITY_IDX", columnList = "authority"),
        @Index(name = "MEMBER_EMAIL_DOMAIN", columnList = "domain")
})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    @Column(name = "NICK_NAME")
    private String nickname;
    @Column(name = "ABOUT_ME")
    private String aboutMe;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "CONTACT", nullable = false)
    private String contact;
    @Embedded
    private ProfileImage profileImage;
    @Column(name = "AUTHORITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "CREATE_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;
    /**
     * 인덱싱용 이메일 도메인 필드
     */
    @Column(name = "DOMAIN")
    private String domain;

    @Builder(builderMethodName = "createMember")
    public Member(String email, String nickname, String password, String contact, ProfileImage profileImage, Authority authority) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.aboutMe = "자기 소개를 등록할 수 있습니다.";
        this.profileImage = profileImage;
        this.contact = contact;
        this.authority = authority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domain = extractDomainFromEmail(email);
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

    /**
     * username 생성
     */
    @PrePersist
    private void generateUsernameWithUUID() {
        this.username = UUID.randomUUID().toString();
    }

    /**
     * username 변경
     */
    public void replaceUsername() {
        generateUsernameWithUUID();
    }

    /**
     * Entity 업데이트 직전 수행
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changePermissions(Authority authority) {
        this.authority = authority;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeContact(String contact) {
        this.contact = contact;
    }

}
