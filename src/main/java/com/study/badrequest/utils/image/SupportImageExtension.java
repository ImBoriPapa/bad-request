package com.study.badrequest.utils.image;

import lombok.Getter;

@Getter
public enum SupportImageExtension {

    PNG(".png"),
    JPEG(".jpeg"),
    JPG(".jpg"),
    GIF(".gif");

    private String extension;

    SupportImageExtension(String extension) {
        this.extension = extension;
    }

}
