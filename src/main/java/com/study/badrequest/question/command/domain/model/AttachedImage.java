package com.study.badrequest.question.command.domain.model;

import lombok.Getter;

@Getter
public class AttachedImage {
    private final Long id;
    private final String image;
    private final Boolean isTemp;

    public AttachedImage(Long id, String image, Boolean isTemp) {
        this.id = id;
        this.image = image;
        this.isTemp = isTemp;
    }

    public AttachedImage tempToSave() {
        return new AttachedImage(getId(), getImage(), false);
    }
}
