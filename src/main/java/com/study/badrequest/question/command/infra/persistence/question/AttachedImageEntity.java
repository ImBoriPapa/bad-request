package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.AttachedImage;
import com.study.badrequest.question.command.domain.model.InitializeAttachedImage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AttachedImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String originalName;
    private String storedName;
    private String type;
    private Long size;
    private Boolean isTemp;
    private LocalDateTime storedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionEntity question;

    @Builder(access = AccessLevel.PROTECTED)
    protected AttachedImageEntity(Long id, String path, String originalName, String storedName, String type, Long size, Boolean isTemp, LocalDateTime storedAt, QuestionEntity question) {
        this.id = id;
        this.path = path;
        this.originalName = originalName;
        this.storedName = storedName;
        this.type = type;
        this.size = size;
        this.isTemp = isTemp;
        this.storedAt = storedAt;
        this.question = question;
    }

    public static AttachedImageEntity fromModel(AttachedImage attachedImage) {
        return AttachedImageEntity.builder()
                .id(attachedImage.getId())
                .path(attachedImage.getPath())
                .originalName(attachedImage.getOriginalName())
                .storedName(attachedImage.getStoredName())
                .type(attachedImage.getType())
                .size(attachedImage.getSize())
                .isTemp(attachedImage.getIsTemp())
                .storedAt(attachedImage.getStoredAt())
                .question(QuestionEntity.formModel(attachedImage.getQuestion()))
                .build();
    }

    public AttachedImage toModel() {
        InitializeAttachedImage initializeAttachedImage = new InitializeAttachedImage(getId(), getPath(), getOriginalName(), getStoredName(), getType(), getSize(), getIsTemp(), getStoredAt(), getQuestion().toModel());
        return AttachedImage.initialize(initializeAttachedImage);
    }
}
