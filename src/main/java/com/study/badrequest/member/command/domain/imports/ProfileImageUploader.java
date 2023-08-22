package com.study.badrequest.member.command.domain.imports;

import com.study.badrequest.member.command.domain.model.ProfileImage;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUploader {
    ProfileImage getDefaultProfileImage();
    ProfileImage uploadProfileImage(MultipartFile imageFile);
    void deleteProfileImageByStoredName(String storedName);
}
