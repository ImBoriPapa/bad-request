package com.study.badrequest.utils.image;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Profile(value = {"dev", "test"})
@Slf4j
public class LocalImageUploader implements ImageUploader {
    // TODO: 2023/01/21 저장위치 고민
    private String path = "";

    @Override
    public List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName) {
        log.info("[LocalImageUploader.uploadFile]");
        List<ImageDetailDto> list = new ArrayList<>();

        for (MultipartFile file : images) {
            String extension = getFileExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + extension;
            String fullPath = path + storedName;

            fileTransfer(file, fullPath);

            ImageDetailDto imageDetailDto = ImageDetailDto.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedName)
                    .fullPath(fullPath)
                    .size(file.getSize())
                    .fileType(file.getContentType())
                    .build();

            list.add(imageDetailDto);
        }

        return list;
    }

    private void fileTransfer(MultipartFile file, String fullPath) {
        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            log.error("[ERROR MESSAGE ={}]", e.getMessage());
            throw new IllegalArgumentException("파일 저장 실패");
        }
    }

    @Override
    public String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    @Override
    public String getFileExtension(String originalFileName) {

        if (originalFileName.lastIndexOf(".") < 0) {
            throw new ImageFileUploadException(CustomStatus.ERROR);
        }

        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));

        return Arrays.stream(SupportImageExtension.values())
                .filter(d -> d.getExtension().equals(ext.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new ImageFileUploadException(CustomStatus.ERROR))
                .getExtension();
    }

    @Override
    public void deleteFile(String storedName) {
        File target = new File(path + storedName);
        target.delete();
    }

    @Override
    public void deleteFile(List<String> storedNameList) {
        storedNameList.forEach(data -> new File(path, data).delete());
    }
}
