package com.study.badrequest.member.command.domain.model;

import lombok.Getter;

@Getter
public final class ProfileImage {
    private final String storedFileName;
    private final String imageLocation;
    private final Long size;
    private final Boolean isDefault;
    public ProfileImage(String storedFileName, String imageLocation, Long size, Boolean isDefault) {
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.isDefault = isDefault;
    }
}
