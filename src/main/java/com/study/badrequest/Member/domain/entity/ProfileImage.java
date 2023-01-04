package com.study.badrequest.Member.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ProfileImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
