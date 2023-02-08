package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Component
@Slf4j
@Profile("prod")
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private String bucket = "bori-market-bucket";
    private String path = "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/";

    private String default_profile_image = "default/profile.jpg";
    private final AmazonS3Client amazonS3Client;

    public List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName) {
        log.info("[S3ImageUploader -> uploadFile()]");

        return getImageDetailList(images, folderName);
    }

    public String getDefaultProfileImage() {
        return amazonS3Client.getResourceUrl(this.bucket, default_profile_image);
    }

    private ArrayList<ImageDetailDto> getImageDetailList(List<MultipartFile> images, String folderName) {

        ArrayList<ImageDetailDto> details = new ArrayList<>();

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

    /**
     * 폴더 경로 포함 저장 위치
     */
    private String getStoredName(String folderName, MultipartFile file) {
        return folderName + File.separator + createFileName(file.getOriginalFilename());
    }

    /**
     * S3 이미지 저장소에 업로드
     */
    private void putImage(MultipartFile file, String storedName) {

        ObjectMetadata objectMetadata = getObjectMetadata(file);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(
                    new PutObjectRequest(
                            bucket,
                            storedName,
                            inputStream,
                            objectMetadata
                    )
                            .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new ImageFileUploadException(CustomStatus.UPLOAD_FAIL_ERROR);
        }
    }

    /**
     * MultipartFile 로 메타 데이터 생성
     */
    private static ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }


    public String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }


    public String getFileExtension(String originalFileName) {

        hasExtension(originalFileName);
        /**
         * 확장자 소문자로 변경
         */
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        isSupportExtension(ext);

        return ext;
    }

    /**
     * 확장자가 있는 파일인지 확인
     */
    private void hasExtension(String originalFileName) {
        if (originalFileName.lastIndexOf(".") < 0) {
            log.info("[확장자가 없는 파일명={}]", originalFileName);
            throw new ImageFileUploadException(CustomStatus.WRONG_FILE_ERROR);
        }
    }

    /**
     * 지원하는 이미지 형식인지 확인
     */
    private void isSupportExtension(String ext) {
        Arrays.stream(SupportImageExtension.values())
                .filter(d -> d.getExtension().equals(ext))
                .findFirst()
                .orElseThrow(() -> new ImageFileUploadException(CustomStatus.NOT_SUPPORT_ERROR));
    }

    public void deleteFile(String storedName) {
        log.info("[deleteFile]");
        amazonS3Client.deleteObject(new DeleteObjectRequest(path, storedName));
    }


    public void deleteFile(List<String> storedNameList) {
        log.info("[deleteFile]");
        storedNameList.forEach(list -> new DeleteObjectRequest(bucket, list));
    }
}
