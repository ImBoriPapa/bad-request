package com.study.badrequest.image.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.image.command.domain.ImageStatus;
import com.study.badrequest.image.command.domain.QuestionImage;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.dto.image.QuestionImageResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.image.command.domain.QuestionImageRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.image.command.infra.ImageUploadDto;
import com.study.badrequest.image.command.infra.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class QuestionImageServiceImpl implements QuestionImageService {
    private final ImageUploader imageUploader;
    private final QuestionRepository questionRepository;
    private final QuestionImageRepository questionImageRepository;
    private final String FOLDER_NAME = "QUESTIONS";
    @Transactional
    public QuestionImageResponse.Temporary saveTemporaryImage(MultipartFile image) {
        log.info("질문 게시글 이미지 임시 저장 원본파일명: {}, 사이즈: {}", image.getOriginalFilename(), image.getSize());

        ImageUploadDto uploadedImage = imageUploader.uploadImageFile(image, FOLDER_NAME);

        QuestionImage savedImage = questionImageRepository.save(QuestionImage.createTemporaryImage(uploadedImage.getOriginalFileName(), uploadedImage.getStoredFileName(), uploadedImage.getImageLocation(), uploadedImage.getSize(), uploadedImage.getFileType()));

        return new QuestionImageResponse.Temporary(savedImage.getId(), savedImage.getOriginalFileName(), savedImage.getImageLocation(), savedImage.getSavedAt());
    }


    @Transactional
    public void changeTemporaryToSaved(Long questionId,List<Long> imageIds) {
        log.info("질문 게시판 임시 이미지 저장완료로 변경");

        List<QuestionImage> images = questionImageRepository.findAllById(imageIds);

        if (!images.isEmpty()) {

            Question question = questionRepository.findById(questionId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_QUESTION));

            images.forEach(image -> {
                log.info("이미지 id:{}, storedName: {} 저장완료로 변경", image.getId(), image.getStoredFileName());
                image.changeTemporaryToSaved(question);
            });
        }
    }


    private void removeAllByQuestion(Question question) {

        List<QuestionImage> findByQuestion = questionImageRepository.findByQuestion(question);

        if (!findByQuestion.isEmpty()) {

            findByQuestion.forEach(image -> imageUploader.deleteFileByStoredNames(image.getStoredFileName()));

            questionImageRepository.deleteAll(findByQuestion);

        }
    }

    @Override
    @Transactional
    public void update(List<Long> imageIds, Long questionId) {
        log.info("이미지 업데이트 시작");
        //수정 요청에 imageId가 없다면 전체 삭제

        Question question = questionRepository.findById(questionId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_QUESTION));

        if (imageIds.isEmpty()) {
            log.info("요청된 이미지 없음");
            removeAllByQuestion(question);
        } else {
            List<QuestionImage> savedImages = questionImageRepository.findByQuestion(question);

            // 저장된 이미지가 없다면 수정 요청된 이미지 전체 저장
            if (savedImages.isEmpty()) {
                questionImageRepository.findAllById(imageIds).forEach(image -> image.changeTemporaryToSaved(question));
            }

            List<Long> savedIds = savedImages.stream().map(QuestionImage::getId).collect(Collectors.toList());

            //저장된 이미지중에 수정 요청된 이미지와 일치하지 않는다면 삭제
            List<Long> toDelete = savedIds.stream()
                    .filter(image -> !imageIds.contains(image))
                    .collect(Collectors.toList());
            questionImageRepository.deleteAllById(toDelete);
            //수정 요청된 이미지중 임시저장파일을 저장완료로 변경
            questionImageRepository.findAllById(imageIds)
                    .forEach(image -> {
                        if (image.getStatus() == ImageStatus.TEMPORARY) {
                            image.changeTemporaryToSaved(question);
                        }
                    });
        }
    }
}
