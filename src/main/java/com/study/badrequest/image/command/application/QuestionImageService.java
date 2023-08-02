package com.study.badrequest.image.command.application;

import com.study.badrequest.dto.image.QuestionImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionImageService {

    QuestionImageResponse.Temporary saveTemporaryImage(MultipartFile image);

    void changeTemporaryToSaved(Long questionId,List<Long> imageIds);

    void update(List<Long> imageIds, Long questionId);
}
