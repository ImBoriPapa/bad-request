package com.study.badrequest.member.command.infra.uploader;

import com.study.badrequest.image.command.domain.ImageUploader;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.member.command.domain.ProfileImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileImageUploaderImpl implements ProfileImageUploader {
    private final ImageUploader imageUploader;

    public ProfileImage getDefaultProfileImage() {
        final String defaultProfileImage = imageUploader.getDefaultProfileImage();
        return ProfileImage.createDefaultImage(defaultProfileImage);
    }
}
