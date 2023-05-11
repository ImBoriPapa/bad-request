package com.study.badrequest.domain.login.company;

import com.study.badrequest.domain.login.Oauth2UserInformation;
import com.study.badrequest.domain.login.OauthProvider;

import java.util.Map;

public class Github extends Oauth2UserInformation {

    public Github(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProvider() {
        return OauthProvider.GITHUB.name();
    }
}