package com.study.badrequest.domain.Member.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Embeddable
@NoArgsConstructor
@Builder
@Getter
public class ProfileImage {

    @Column(name = "ORIGINAL_NAME")
    private String originalFileName;

    @Column(name = "STORED_NAME")
    private String storedFileName;

    @Column(name = "FULL_PATH")
    private String fullPath;

    @Column(name = "SIZE")
    private Long size;

    @Column(name = "TYPE")
    private String fileType;

    public ProfileImage(String originalFileName, String storedFileName, String fullPath, Long size, String fileType) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fullPath = fullPath;
        this.size = size;
        this.fileType = fileType;
    }
}
