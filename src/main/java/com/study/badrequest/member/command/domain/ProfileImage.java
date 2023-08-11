package com.study.badrequest.member.command.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProfileImage {
    @Column(name = "stored_file_name")
    private String storedFileName;
    @Column(name = "image_location")
    private String imageLocation;
    @Column(name = "size")
    private Long size;
    @Column(name = "is_default")
    private Boolean isDefault;

    protected ProfileImage(String storedFileName, String imageLocation, Long size, Boolean isDefault) {
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.isDefault = isDefault;
    }

    public static ProfileImage createDefaultImage(String imageLocation) {
        return new ProfileImage("default image", imageLocation, 1L, true);
    }

    public static ProfileImage createProfileImage(String storedFileName, String imageLocation, Long size) {
        return new ProfileImage(storedFileName, imageLocation, size, false);
    }

    public void replaceDefaultImage(String imageLocation) {
        this.storedFileName = "default image";
        this.imageLocation = imageLocation;
        this.size = 1L;
        this.isDefault = true;
    }

}
