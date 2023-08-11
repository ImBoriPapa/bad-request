package com.study.badrequest.member.command.infra.uploader;

import com.study.badrequest.image.command.domain.ImageUploadDto;
import com.study.badrequest.image.command.domain.ImageUploader;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.member.command.domain.ProfileImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProfileImageUploaderImpl implements ProfileImageUploader {
    private final ImageUploader imageUploader;

    private final String FOLDER_NAME = "profile";

    @Override
    public ProfileImage getDefaultProfileImage() {
        final String defaultProfileImage = imageUploader.getDefaultProfileImage();
        return ProfileImage.createDefaultImage(defaultProfileImage);
    }

    @Override
    public ProfileImage uploadProfileImage(MultipartFile imageFile) {

        ImageUploadDto imageUploadDto = imageUploader.uploadImageFile(imageFile, FOLDER_NAME);

        return ProfileImage.createProfileImage(
                imageUploadDto.getStoredFileName(),
                imageUploadDto.getImageLocation(),
                imageUploadDto.getSize());
    }

    @Override
    public void deleteProfileImageByStoredName(String storedName) {
        imageUploader.deleteFileByStoredName(storedName);
    }


}
