package com.study.badrequest.member.command.infra.profileImage;

import com.study.badrequest.image.command.infra.ImageUploader;
import com.study.badrequest.member.command.domain.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileImageServiceImpl {
    private final ImageUploader imageUploader;

    public ProfileImage getDefaultProfileImage() {
        final String defaultProfileImage = imageUploader.getDefaultProfileImage();
        return ProfileImage.createDefaultImage(defaultProfileImage);
    }
}
