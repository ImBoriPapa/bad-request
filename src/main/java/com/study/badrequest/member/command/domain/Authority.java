package com.study.badrequest.member.command.domain;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.exception.CustomRuntimeException;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 회원 권한
 */
@Getter
public enum Authority {
    MEMBER("ROLE_MEMBER"),
    TEACHER("ROLE_MEMBER,ROLE_TEACHER"),
    ADMIN("ROLE_MEMBER,ROLE_TEACHER,ROLE_ADMIN");

    private final String role;

    Authority(String role) {
        this.role = role;
    }

    public List<String> getRoleList() {
        return new ArrayList<>(Arrays.asList(this.role.split(",")));
    }

    /**
     * Authority -> Collection<? extends GrantedAuthority>
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        this.getRoleList()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        return authorities;
    }

    /**
     * Collection<? extends GrantedAuthority> authorities -> Authority
     * ROLE_ADMIN 이 있으면 Authority.ADMIN 반환
     * ROLE_TEACHER 이 있으면 Authority.TEACHER 반환
     * ROLE_MEMBER 이 있으면 Authority.MEMBER 반환
     */
    public static Authority getAuthorityByAuthorities(Collection<? extends GrantedAuthority> authorities) {

        final String ROLE_ADMIN = "ROLE_ADMIN";
        final String ROLE_TEACHER = "ROLE_TEACHER";
        final String ROLE_MEMBER = "ROLE_MEMBER";

        List<String> collect = authorities.stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Authority admin = getAuthorityContainsString(collect, ROLE_ADMIN, Authority.ADMIN);
        if (admin != null) return admin;

        Authority teacher = getAuthorityContainsString(collect, ROLE_TEACHER, Authority.TEACHER);
        if (teacher != null) return teacher;

        Authority member = getAuthorityContainsString(collect, ROLE_MEMBER, Authority.MEMBER);
        if (member != null) return member;

        throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.WRONG_AUTHORITY);
    }

    private static Authority getAuthorityContainsString(List<String> collect, String ROLE_ADMIN, Authority admin) {
        if (collect.contains(ROLE_ADMIN)) {
            return admin;
        }
        return null;
    }

}
