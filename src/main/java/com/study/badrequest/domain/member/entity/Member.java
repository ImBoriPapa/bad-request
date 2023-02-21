package com.study.badrequest.domain.member.entity;

import com.fasterxml.uuid.Generators;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@Table(name = "MEMBER", indexes = @Index(name = "MEMBER_AUTHORITY_IDX", columnList = "authority"))
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
     * ReturnType String SpringSecurity 에서 Type Converting 없이 사용
     * 시간순 정렬 UUID
     * <p>
     * 2/15 @PrePersist,AtomicLong 동시성 문제 방지 하기 위해 사용
     */
    private final static AtomicLong USERNAME_SEQUENCE = new AtomicLong();

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
    }

    /**
     * AtomicLong : USERNAME_SEQUENCE 로 username 생성하고
     * entity 영속되기 직전에 username 초기화 하여 동시성 이슈 회피
     * 동시성 문제를 더 테스트해보고 계속 사용해될지 고려
     */
    @PrePersist
    private void generateSequentialUUID() {
        String proto = Generators.timeBasedGenerator().generate().toString();
        String[] array = proto.split("-");

        if (USERNAME_SEQUENCE.incrementAndGet() == Long.MAX_VALUE) {
            USERNAME_SEQUENCE.set(0);
        }

        String sort = array[2] + array[1] + array[0] + array[3] + USERNAME_SEQUENCE.incrementAndGet();
        this.username = new StringBuilder(sort)
                .insert(8, "-")
                .insert(13, "-")
                .insert(18, "-")
                .insert(23, "-")
                .toString();
    }

    /**
     * username 변경
     */
    public void replaceUsername() {
        generateSequentialUUID();
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
