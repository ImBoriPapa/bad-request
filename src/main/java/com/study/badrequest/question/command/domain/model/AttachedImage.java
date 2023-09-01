package com.study.badrequest.question.command.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AttachedImage {
    private final Long id;
    private final String path;
    private final String originalName;
    private final String storedName;
    private final String type;
    private final Long size;
    private final Boolean isTemp;
    private final Question question;
    private final LocalDateTime storedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private AttachedImage(Long id, String path, String originalName, String storedName, String type, Long size, Boolean isTemp, Question question, LocalDateTime storedAt) {
        this.id = id;
        this.path = path;
        this.originalName = originalName;
        this.storedName = storedName;
        this.type = type;
        this.size = size;
        this.isTemp = isTemp;
        this.question = question;
        this.storedAt = storedAt;
    }

    public static AttachedImage createAttachedImage(String path, String originalName, String storedName, String type, Long size) {
        return AttachedImage.builder()
                .path(path)
                .originalName(originalName)
                .storedName(storedName)
                .type(type)
                .size(size)
                .isTemp(true)
                .storedAt(LocalDateTime.now())
                .build();
    }

    public static AttachedImage initialize(InitializeAttachedImage attachedImage) {
        return AttachedImage.builder()
                .id(attachedImage.id())
                .path(attachedImage.path())
                .originalName(attachedImage.originalName())
                .storedName(attachedImage.storedName())
                .type(attachedImage.type())
                .size(attachedImage.size())
                .isTemp(attachedImage.isTemp())
                .storedAt(attachedImage.storedAt())
                .question(attachedImage.question())
                .build();
    }

    public AttachedImage tempToSave(final Question question) {
        return AttachedImage.builder()
                .id(getId())
                .path(getPath())
                .originalName(getOriginalName())
                .storedName(getStoredName())
                .type(getType())
                .size(getSize())
                .isTemp(false)
                .question(question)
                .build();
    }
}
