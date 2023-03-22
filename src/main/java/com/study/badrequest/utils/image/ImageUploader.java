package com.study.badrequest.utils.image;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {

    ImageUplaodDto uploadFile(MultipartFile image, String folderName);
    List<ImageUplaodDto> uploadFile(List<MultipartFile> images, String folderName);

    String getDefaultProfileImage();

    void deleteFileByStoredNames(String storedName);

    void deleteFileByStoredNames(List<String> storedNameList);
}
