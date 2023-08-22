package com.study.badrequest.login.command.domain.company;

import com.study.badrequest.login.command.domain.Oauth2UserInformation;
import com.study.badrequest.member.command.domain.values.RegistrationType;

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
        return RegistrationType.GITHUB.name();
    }
}
