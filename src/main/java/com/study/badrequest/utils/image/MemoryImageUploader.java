package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class MemoryImageUploader implements ImageUploader{

    private String bucket = "bori-market-bucket";
    private String path = "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/";
    private final MemoryS3 memoryS3;

    public List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName) {
        log.info("[LocalImageUploader -> uploadFile()]");

        List<ImageDetailDto> details = new ArrayList<>();

        images.forEach(file -> {
            String storedName = getStoredName(folderName, file);
            putImage(file, storedName);
            details.add(
                    ImageDetailDto
                            .builder()
                            .originalFileName(file.getOriginalFilename())
                            .storedFileName(storedName)
                            .fullPath(path + storedName)
                            .fileType(file.getContentType())
                            .size(file.getSize())
                            .build());
        });

        return details;
    }

    private String getStoredName(String folderName, MultipartFile file) {
        return folderName + File.separator + createFileName(file.getOriginalFilename());
    }

    private void putImage(MultipartFile file, String storedName) {
        log.info("[LocalImageUploader -> fileTransfer]");

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            memoryS3.putObjectRequest(
                    bucket,
                    storedName,
                    file.getInputStream(),
                    objectMetadata);
        } catch (IOException e) {
            throw new ImageFileUploadException(CustomStatus.UPLOAD_FAIL_ERROR);
        }

    }

    public String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }


    public String getFileExtension(String originalFileName) {
        log.info("[originalFileName= {}]", originalFileName);

        if (originalFileName.lastIndexOf(".") < 0) {
            log.info("[확장자가 없는 파일명= {}]", originalFileName);
            throw new ImageFileUploadException(CustomStatus.WRONG_FILE_ERROR);
        }

        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));

        log.info("[ext]={}", ext);
        return Arrays.stream(SupportImageExtension.values())
                .filter(d -> d.getExtension().equals(ext.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new ImageFileUploadException(CustomStatus.NOT_SUPPORT_ERROR)).getExtension();
    }


    public void deleteFile(String storedName) {
        log.info("[IMAGE DELETE imageName= {}]", storedName);
        File target = new File(path + storedName);
        target.delete();
    }


    public void deleteFile(List<String> storedNameList) {
        log.info("[IMAGE DELETE imageNameList]");
        storedNameList.forEach(data -> new File(path, data).delete());
    }

}
