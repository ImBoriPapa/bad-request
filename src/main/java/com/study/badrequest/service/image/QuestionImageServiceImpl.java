package com.study.badrequest.service.image;

import com.study.badrequest.domain.image.QuestionImage;
import com.study.badrequest.dto.image.QuestionImageResponse;
import com.study.badrequest.repository.image.QuestionImageRepository;
import com.study.badrequest.utils.image.ImageUploadDto;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class QuestionImageServiceImpl implements QuestionImageService{

    private final S3ImageUploader imageUploader;
    private final QuestionImageRepository questionImageRepository;

    @Transactional
    public QuestionImageResponse.Temporary saveTemporaryImage(MultipartFile image) {

        log.info("질문 게시글 이미지 임시 저장 원본파일명: {}, 사이즈: {}", image.getOriginalFilename(), image.getSize());

        ImageUploadDto question = imageUploader.uploadFile(image, "question");

        QuestionImage savedImage = questionImageRepository.save(QuestionImage.createTemporaryImage(question.getOriginalFileName(), question.getStoredFileName(), question.getImageLocation(), question.getSize(), question.getFileType()));

        return new QuestionImageResponse.Temporary(savedImage.getId(), savedImage.getOriginalFileName(), savedImage.getImageLocation(), savedImage.getSavedAt());
    }

    @Transactional
    public void chaneTemporaryToSaved(List<Long> imageIds) {

        log.info("질문 게시판 임시 이미지 저장완료로 변경");

        List<QuestionImage> images = questionImageRepository.findAllById(imageIds);

        if (!images.isEmpty()) {
            images.forEach(QuestionImage::changeToSaved);
        }

    }
}
