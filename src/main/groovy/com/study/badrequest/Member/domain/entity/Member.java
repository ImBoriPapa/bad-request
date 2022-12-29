package com.study.badrequest.Member.domain.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

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
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "AUTHORITY")
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PROFILE_ID")
    private Profile profile;

    @Column(name = "CREATE_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "createMember")
    public Member(String email, String password, String name, String contact, Authority authority, Profile profile) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.authority = authority;
        this.profile = profile;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public enum Authority {
        USER("ROLL_MEMBER"),
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

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
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
        if (StringUtils.hasLength(contact)) {
            this.contact = contact;
            this.updatedAt = LocalDateTime.now();
        }
    }

}
