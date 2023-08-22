package com.study.badrequest.member.command.domain.values;

import com.study.badrequest.login.command.domain.Oauth2UserInformation;
import com.study.badrequest.login.command.domain.company.Github;
import com.study.badrequest.login.command.domain.company.Google;
import com.study.badrequest.login.command.domain.company.Kakao;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
@Getter
public enum RegistrationType {

    GITHUB("Github") {
        @Override
        public Oauth2UserInformation of(Map<String, Object> attributes) {
            return new Github(attributes);
        }
    },
    GOOGLE("Google") {
        @Override
        public Oauth2UserInformation of(Map<String, Object> attributes) {
            return new Google(attributes);
        }
    },
    KAKAO("Kakao") {
        @Override
        public Oauth2UserInformation of(Map<String, Object> attributes) {
            return new Kakao(attributes);
        }
    },
    BAD_REQUEST("bad-request") {
        @Override
        public Oauth2UserInformation of(Map<String, Object> attributes) {
            throw new IllegalArgumentException("Not Supported Oauth");
        }
    };

    private final String providerName;

    RegistrationType(String providerName) {
        this.providerName = providerName;
    }

    public Oauth2UserInformation of(Map<String, Object> attributes) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public static Oauth2UserInformation getOauth2UserInformation(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(oauthProvider -> oauthProvider.providerName.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Provider not found: " + registrationId))
                .of(attributes);
    }

    public static RegistrationType getOauth2UserInformation(String oauthProvider) {
        return Arrays.stream(values())
                .filter(oauthProvider1 -> oauthProvider1.providerName.equalsIgnoreCase(oauthProvider))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Provider not found: " + oauthProvider));
    }
}
