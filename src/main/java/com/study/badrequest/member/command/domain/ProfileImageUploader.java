package com.study.badrequest.member.command.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUploader {
    ProfileImage getDefaultProfileImage();
    ProfileImage uploadProfileImage(MultipartFile imageFile);
    void deleteProfileImageByStoredName(String storedName);
}
