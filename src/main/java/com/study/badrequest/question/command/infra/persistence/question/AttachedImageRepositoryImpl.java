package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.AttachedImage;
import com.study.badrequest.question.command.domain.repository.AttachedImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AttachedImageRepositoryImpl implements AttachedImageRepository {

    private final AttachedImageJpaRepository attachedImageJpaRepository;

    @Override
    public AttachedImage save(AttachedImage attachedImage) {
        return attachedImageJpaRepository.save(AttachedImageEntity.fromModel(attachedImage)).toModel();
    }

    @Override
    public List<AttachedImage> saveAll(List<AttachedImage> attachedImages) {
        List<AttachedImageEntity> entities = attachedImages.stream().map(AttachedImageEntity::fromModel).collect(Collectors.toList());
        return attachedImageJpaRepository.saveAll(entities).stream().map(AttachedImageEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<AttachedImage> findAllByIdIn(List<Long> ids) {
        return attachedImageJpaRepository.findAllByIdIn(ids).stream().map(AttachedImageEntity::toModel).collect(Collectors.toList());
    }
}
