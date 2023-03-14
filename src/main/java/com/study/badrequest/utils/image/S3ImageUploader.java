package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.ImageFileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private static final  String BUCKET_NAME = "bori-market-bucket";
    private static final String BUCKET_URL = "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/";
    private static final String DEFAULT_PROFILE_IMAGE = "default/profile.jpg";
    private final AmazonS3Client amazonS3Client;

    public ImageDetailDto uploadFile(MultipartFile image, String folderName) {
        return uploadImage(image, folderName);
    }

    /**
     * upload File List
     */
    public List<ImageDetailDto> uploadFile(List<MultipartFile> images, String folderName) {
        log.info("[S3ImageUploader -> uploadFile()]");
        return images
                .stream()
                .map(file -> uploadImage(file, folderName))
                .collect(Collectors.toList());
    }

    /**
     * 기본 프로필이미지 반환
     */
    public String getDefaultProfileImage() {
        return amazonS3Client.getResourceUrl(BUCKET_NAME, DEFAULT_PROFILE_IMAGE);
    }

    /**
     * 이미지 업로드 후 ImageDto 반환
     */
    private ImageDetailDto uploadImage(MultipartFile file, String folderName) {

        String storedName = getStoredName(folderName, file);

        ObjectMetadata objectMetadata = getObjectMetadata(file);

        putImage(file, storedName, objectMetadata);

        return ImageDetailDto
                .builder()
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedName)
                .fullPath(BUCKET_URL + storedName)
                .fileType(file.getContentType())
                .size(file.getSize())
                .build();
    }


    /**
     * 폴더 경로 포함 저장 위치로 저장파일명 생성
     */
    private String getStoredName(String folderName, MultipartFile file) {
        return folderName + File.separator + createFileName(file.getOriginalFilename());
    }

    /**
     * S3 이미지 저장소에 업로드
     */
    private void putImage(MultipartFile file, String storedName, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(BUCKET_NAME, storedName, inputStream, objectMetadata)
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

    /**
     * UUID 로 파일명 생성
     */
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    /**
     * 확장자 소문자로 변경후 추출
     */
    public String getFileExtension(String originalFileName) {

        hasExtension(originalFileName);

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
        if (Arrays.stream(SupportImageExtension.values())
                .noneMatch(d -> d.getExtension().equals(ext))) {
            throw new ImageFileUploadException(CustomStatus.NOT_SUPPORT_ERROR);
        }
    }

    /**
     * 이미지 단건 삭제
     */
    public void deleteFile(String storedName) {
        log.info("[deleteFile]");
        if (storedName != null) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName));
        }

    }

    /**
     * 이미지 여러건 삭제
     */
    public void deleteFile(List<String> storedNameList) {
        log.info("[deleteFile]");
        if (storedNameList != null) {
            storedNameList.forEach(storedName -> amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName)));
        }
    }
}
