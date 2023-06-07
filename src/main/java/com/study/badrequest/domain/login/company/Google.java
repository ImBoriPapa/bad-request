package com.study.badrequest.domain.login.company;

import com.study.badrequest.domain.login.Oauth2UserInformation;
import com.study.badrequest.domain.member.RegistrationType;

import java.util.Map;

public class Google extends Oauth2UserInformation {

    public Google(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProvider() {
        return RegistrationType.GOOGLE.name();
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
