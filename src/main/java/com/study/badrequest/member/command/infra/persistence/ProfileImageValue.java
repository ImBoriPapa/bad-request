package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.model.ProfileImage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProfileImageValue {
    @Column(name = "stored_file_name")
    private String storedFileName;
    @Column(name = "image_location")
    private String imageLocation;
    @Column(name = "size")
    private Long size;
    @Column(name = "is_default")
    private Boolean isDefault;

    protected ProfileImageValue(String storedFileName, String imageLocation, Long size, Boolean isDefault) {
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.isDefault = isDefault;
    }

    public static ProfileImageValue createDefaultImage(String imageLocation) {
        return new ProfileImageValue("default image", imageLocation, 1L, true);
    }

    public static ProfileImageValue createProfileImage(String storedFileName, String imageLocation, Long size) {
        return new ProfileImageValue(storedFileName, imageLocation, size, false);
    }

    public static ProfileImageValue fromModel(ProfileImage profileImage) {
        return new ProfileImageValue(profileImage.getStoredFileName(), profileImage.getImageLocation(), profileImage.getSize(), profileImage.getIsDefault());
    }

    public void replaceDefaultImage(String imageLocation) {
        this.storedFileName = "default image";
        this.imageLocation = imageLocation;
        this.size = 1L;
        this.isDefault = true;
    }

    public ProfileImage toModel() {
        return new ProfileImage(getStoredFileName(),getImageLocation(),getSize(),getIsDefault());
    }
}
