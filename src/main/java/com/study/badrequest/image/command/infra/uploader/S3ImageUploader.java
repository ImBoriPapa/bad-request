package com.study.badrequest.image.command.infra.uploader;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.study.badrequest.common.exception.CustomRuntimeException;

import com.study.badrequest.image.command.domain.ImageUploadDto;
import com.study.badrequest.image.command.domain.ImageUploader;
import com.study.badrequest.image.command.domain.SupportImageExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private final AmazonS3Client amazonS3Client;
    @Value("${s3-image.bucket-name}")
    private String BUCKET_NAME;
    @Value("${s3-image.bucket-url}")
    private String BUCKET_URL;
    @Value("${s3-image.default-profile-image}")
    private String DEFAULT_PROFILE_IMAGE;

    public ImageUploadDto uploadImageFile(MultipartFile image, String folderName) {

        String storedName = getStoredName(folderName, image);

        putImageToStorage(image, storedName, getObjectMetadata(image));

        return ImageUploadDto
                .builder()
                .originalFileName(image.getOriginalFilename())
                .storedFileName(storedName)
                .imageLocation(BUCKET_URL + storedName)
                .fileType(image.getContentType())
                .size(image.getSize())
                .build();
    }

    public List<ImageUploadDto> uploadImageFile(List<MultipartFile> images, String folderName) {
        log.info("[S3ImageUploader -> uploadFile()]");
        return images
                .stream()
                .map(file -> uploadImageFile(file, folderName))
                .collect(Collectors.toList());
    }

    public void deleteFileByStoredNames(String storedName) {
        log.info("[deleteFile]");
        if (storedName != null) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName));
        }
    }

    public void deleteFileByStoredNames(List<String> storedNameList) {
        log.info("[deleteFile]");
        if (storedNameList != null) {
            storedNameList.forEach(storedName -> amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName)));
        }
    }

    public String getDefaultProfileImage() {

        ImageUploadDto dto = new ImageUploadDto();

        return DEFAULT_PROFILE_IMAGE;
    }


    private String getStoredName(String folderName, MultipartFile file) {
        return folderName + File.separator + createFileName(file.getOriginalFilename());
    }


    private void putImageToStorage(MultipartFile file, String storedName, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = file.getInputStream()) {

            amazonS3Client.putObject(
                    new PutObjectRequest(
                            BUCKET_NAME,
                            storedName,
                            inputStream,
                            objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw CustomRuntimeException.createWithApiResponseStatus(UPLOAD_FAIL_ERROR);
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }


    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }


    public String getFileExtension(String originalFileName) {

        validateExtension(originalFileName);

        String ext = extractExtension(originalFileName);

        validateIsSupportExtension(ext);

        return ext;
    }


    private String extractExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
    }


    private void validateExtension(String originalFileName) {
        if (originalFileName.lastIndexOf(".") < 0) {
            log.info("[확장자가 없는 파일명={}]", originalFileName);
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_FILE_ERROR);
        }
    }


    private void validateIsSupportExtension(String ext) {
        if (Arrays.stream(SupportImageExtension.values())
                .noneMatch(imageExtension -> imageExtension.getExtension().equals(ext))) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_SUPPORT_ERROR);
        }
    }


}
