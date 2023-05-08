package com.study.badrequest.domain.login;

import com.study.badrequest.domain.login.company.Github;
import com.study.badrequest.domain.login.company.Google;
import com.study.badrequest.domain.login.company.Kakao;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
@Getter
public enum OauthProvider {

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

    OauthProvider(String providerName) {
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

    public static OauthProvider getOauth2UserInformation(String oauthProvider) {
        return Arrays.stream(values())
                .filter(oauthProvider1 -> oauthProvider1.providerName.equalsIgnoreCase(oauthProvider))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Provider not found: " + oauthProvider));
    }
}
