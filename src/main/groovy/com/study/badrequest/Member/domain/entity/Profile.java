package com.study.badrequest.Member.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFILE_ID")
    private Long id;
    private String nickname;
    private String aboutMe;
    @Builder
    public Profile(String nickname, String aboutMe) {
        this.nickname = nickname;
        this.aboutMe = aboutMe;
    }
}
