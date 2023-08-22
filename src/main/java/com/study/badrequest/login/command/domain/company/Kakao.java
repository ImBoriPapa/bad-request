package com.study.badrequest.login.command.domain.company;

import com.study.badrequest.login.command.domain.Oauth2UserInformation;
import com.study.badrequest.member.command.domain.values.RegistrationType;

import java.util.Map;

public class Kakao extends Oauth2UserInformation {

    public Kakao(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProvider() {
        return RegistrationType.KAKAO.name();
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");


        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {

        Map<String, Object> properties = (Map<String, Object>) attributes.get("kakao_account");

        return (String) properties.get("email");
    }
}
