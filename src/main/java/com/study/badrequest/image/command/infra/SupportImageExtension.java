package com.study.badrequest.image.command.infra;

import lombok.Getter;

@Getter
public enum SupportImageExtension {

    PNG(".png"),
    JPEG(".jpeg"),
    JPG(".jpg"),
    GIF(".gif");

    private final String extension;

    SupportImageExtension(String extension) {
        this.extension = extension;
    }

}
