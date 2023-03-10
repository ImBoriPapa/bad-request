package com.study.badrequest.utils.image;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {

    ImageDetailDto uploadFile(MultipartFile image, String folderName);
    List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName);

    String getDefaultProfileImage();

    void deleteFile(String storedName);

    void deleteFile(List<String> storedNameList);
}
