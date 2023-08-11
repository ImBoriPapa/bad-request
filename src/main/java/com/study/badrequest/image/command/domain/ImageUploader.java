package com.study.badrequest.image.command.domain;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {

    ImageUploadDto uploadImageFile(MultipartFile image, String folderName);
    List<ImageUploadDto> uploadImageFile(List<MultipartFile> images, String folderName);

    String getDefaultProfileImage();

    void deleteFileByStoredName(String storedName);

    void deleteFileByStoredNames(List<String> storedNameList);
}
