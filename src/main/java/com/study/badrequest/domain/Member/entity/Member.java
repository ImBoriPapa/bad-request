package com.study.badrequest.domain.Member.entity;

import com.fasterxml.uuid.Generators;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "USER_NAME")
    private String username;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "NICK_NAME")
    private String nickname;
    @Column(name = "ABOUT_ME")
    private String aboutMe;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CONTACT")
    private String contact;
    @Embedded
    private ProfileImage profileImage;
    @Column(name = "AUTHORITY")
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "CREATE_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "createMember")
    public Member(String email, String nickname, String aboutMe, String password, String name, String contact,ProfileImage profileImage, Authority authority) {
        this.username = generateSequentialUUID();
        this.email = email;
        this.nickname = nickname;
        this.aboutMe = aboutMe;
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
        this.contact = contact;
        this.authority = authority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        this.getAuthority().getRoleList()
                .forEach(m -> authorities.add(new SimpleGrantedAuthority(m)));

        return authorities;
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(Authority authority) {

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        authority.getRoleList()
                .forEach(m -> authorities.add(new SimpleGrantedAuthority(m)));

        return authorities;
    }

    /**
     * 시간순 정렬 UUID
     */
    private String generateSequentialUUID() {
        String proto = Generators.timeBasedGenerator().generate().toString();
        String[] array = proto.split("-");
        String sort = array[2] + array[1] + array[0] + array[3] + array[4];
        return new StringBuilder(sort)
                .insert(8, "-")
                .insert(13, "-")
                .insert(18, "-")
                .insert(23, "-").toString();
    }


    public void changePermissions(Authority authority) {
        this.authority = authority;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeContact(String contact) {

        this.contact = contact;
        this.updatedAt = LocalDateTime.now();

    }

}
