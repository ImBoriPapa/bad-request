package com.study.badrequest.utils.image;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("test")
public class MemoryImageUploader implements ImageUploader{
    private String location = System.getProperty("user.dir");
    private String bucket = location + "/src/main/resources/static/image";
    private String path = "http://localhost:8080/image/";

    @CustomLogger
    public List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName) {
        log.info("[LocalImageUploader -> uploadFile()]");

        List<ImageDetailDto> details = new ArrayList<>();
        //???
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
        File f = new File(bucket ,storedName);
        try {
            file.transferTo(f);
        } catch (IOException e) {
            log.info("[업로드 에러= {}]", e.getMessage());
            throw new ImageFileUploadException(CustomStatus.WRONG_FILE_ERROR);
        }

        ArrayList<String> store = new ArrayList<>();
        store.add(storedName);
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
        boolean b = target.exists() ? target.delete() : target.exists();
    }


    public void deleteFile(List<String> storedNameList) {
        log.info("[IMAGE DELETE imageNameList]");
        storedNameList.forEach(this::deleteFile);
    }

}
