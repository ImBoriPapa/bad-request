package com.study.badrequest.login.command.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public abstract class Oauth2UserInformation {
    protected Map<String, Object> attributes;
    public Oauth2UserInformation(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getProvider();

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();
}
