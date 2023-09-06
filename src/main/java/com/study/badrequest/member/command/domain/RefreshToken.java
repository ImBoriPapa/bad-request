package com.study.badrequest.member.command.domain;

import lombok.Getter;
@Getter
public final class RefreshToken {
    private final String id;
    private final String token;
    private final long expiration;

    public RefreshToken(String id, String token, long expiration) {
        this.id = id;
        this.token = token;
        this.expiration = expiration;
    }

    public static RefreshToken createRefreshToken(String id, String token, long expiration) {
        return new RefreshToken(id, token, expiration);

    }
}
