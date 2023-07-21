package com.study.badrequest.service.image;

import com.study.badrequest.domain.question.Question;
import com.study.badrequest.dto.image.QuestionImageResponse;
import com.study.badrequest.exception.UrgentCheckedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

public interface QuestionImageService {

    QuestionImageResponse.Temporary saveTemporaryImage(MultipartFile image);

    void changeTemporaryToSaved(Long questionId,List<Long> imageIds);

    void update(List<Long> imageIds, Long questionId);
}
