package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.study.badrequest.exception.CustomRuntimeException;

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

import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader {

    private final AmazonS3Client amazonS3Client;
    @Value("${s3-image.bucket-name}")
    public String BUCKET_NAME;
    @Value("${s3-image.bucket-url}")
    public String BUCKET_URL;
    @Value("${s3-image.default-profile-image}")
    public String DEFAULT_PROFILE_IMAGE;

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

    /**
     * upload File List
     */
    public List<ImageUploadDto> uploadImageFile(List<MultipartFile> images, String folderName) {
        log.info("[S3ImageUploader -> uploadFile()]");
        return images
                .stream()
                .map(file -> uploadImageFile(file, folderName))
                .collect(Collectors.toList());
    }

    /**
     * 이미지 단건 삭제
     */
    public void deleteFileByStoredNames(String storedName) {
        log.info("[deleteFile]");
        if (storedName != null) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName));
        }
    }

    /**
     * 이미지 여러건 삭제
     */
    public void deleteFileByStoredNames(List<String> storedNameList) {
        log.info("[deleteFile]");
        if (storedNameList != null) {
            storedNameList.forEach(storedName -> amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, storedName)));
        }
    }


    /**
     * 기본 프로필이미지 반환
     */
    public String getDefaultProfileImage() {
        return DEFAULT_PROFILE_IMAGE;
    }

    /**
     * 이미지 업로드 후 ImageDto 반환
     */

    /**
     * 폴더 경로 포함 저장 위치로 저장파일명 생성
     */
    private String getStoredName(String folderName, MultipartFile file) {
        return folderName + File.separator + createFileName(file.getOriginalFilename());
    }

    /**
     * S3 이미지 저장소에 업로드
     */
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
            throw new CustomRuntimeException(UPLOAD_FAIL_ERROR);
        }
    }

    /**
     * MultipartFile 로 메타 데이터 생성
     */
    private ObjectMetadata getObjectMetadata(MultipartFile file) {
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

        validateExtension(originalFileName);

        String ext = extractExtension(originalFileName);

        validateIsSupportExtension(ext);

        return ext;
    }


    private String extractExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 확장자가 있는 파일인지 확인
     */
    private void validateExtension(String originalFileName) {
        if (originalFileName.lastIndexOf(".") < 0) {
            log.info("[확장자가 없는 파일명={}]", originalFileName);
            throw new CustomRuntimeException(WRONG_FILE_ERROR);
        }
    }

    /**
     * 지원하는 이미지 형식인지 확인
     */
    private void validateIsSupportExtension(String ext) {
        if (Arrays.stream(SupportImageExtension.values())
                .noneMatch(imageExtension -> imageExtension.getExtension().equals(ext))) {
            throw new CustomRuntimeException(NOT_SUPPORT_ERROR);
        }
    }


}
