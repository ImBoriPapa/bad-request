package com.study.badrequest.Member.domain.entity;

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
public class Member implements UserDetails {
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
    public Member(String email, String nickname, String aboutMe, String password, String name, String contact, Authority authority) {
        this.username = generateSequentialUUID();
        this.email = email;
        this.nickname = nickname;
        this.aboutMe = aboutMe;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.authority = authority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Getter
    public enum Authority {
        MEMBER("ROLL_MEMBER"),
        TEACHER("ROLL_MEMBER,ROLL_TEACHER"),
        ADMIN("ROLL_MEMBER,ROLL_TEACHER,ROLL_ADMIN");

        private final String roll;

        Authority(String roll) {
            this.roll = roll;
        }

        private List<String> getRoleList() {
            ArrayList<String> list = new ArrayList<>();
            Arrays.stream(this.roll.split(","))
                    .forEach(list::add);
            return list;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        this.getAuthority().getRoleList()
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
        StringBuilder builder = new StringBuilder(sort);
        builder.insert(8, "-");
        builder.insert(13, "-");
        builder.insert(18, "-");
        builder.insert(23, "-");
        return builder.toString();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
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
