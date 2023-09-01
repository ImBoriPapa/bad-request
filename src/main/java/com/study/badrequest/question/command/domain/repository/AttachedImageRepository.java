package com.study.badrequest.question.command.domain.repository;

import com.study.badrequest.question.command.domain.model.AttachedImage;

import java.util.List;

public interface AttachedImageRepository {

    AttachedImage save(AttachedImage attachedImage);

    List<AttachedImage> saveAll(List<AttachedImage> attachedImages);

    List<AttachedImage> findAllByIdIn(List<Long> ids);


}
