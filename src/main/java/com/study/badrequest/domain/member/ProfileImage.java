package com.study.badrequest.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProfileImage {
    @Column(name = "STORED_NAME")
    private String storedFileName;
    @Column(name = "IMAGE_LOCATION")
    private String imageLocation;
    @Column(name = "SIZE")
    private Long size;
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    protected ProfileImage(String storedFileName, String imageLocation, Long size, Boolean isDefault) {
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.isDefault = isDefault;
    }

    public static ProfileImage createDefault(String imageLocation) {
        return new ProfileImage("default image", imageLocation, 1L, true);
    }

    public void replaceDefaultImage(String imageLocation) {
        this.storedFileName = "default image";
        this.imageLocation = imageLocation;
        this.size = 1L;
        this.isDefault = true;
    }
    public ProfileImage replaceProfileImage(String storedFileName, String imageLocation, Long size) {
        return new ProfileImage(storedFileName, imageLocation, size, false);
    }
}
