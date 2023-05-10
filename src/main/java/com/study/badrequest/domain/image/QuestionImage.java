package com.study.badrequest.domain.image;

import com.study.badrequest.domain.question.Question;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION_IMAGE")
public class QuestionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_IMAGE_ID")
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String imageLocation;
    private Long size;
    private String fileType;
    @Enumerated(EnumType.STRING)
    private ImageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
    private LocalDateTime savedAt;

    @Builder
    protected QuestionImage(String originalFileName, String storedFileName, String imageLocation, Long size, String fileType, ImageStatus status, Question question, LocalDateTime savedAt) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.fileType = fileType;
        this.status = status;
        this.question = question;
        this.savedAt = savedAt;
    }

    public static QuestionImage createTemporaryImage(String originalFileName, String storedFileName, String imageLocation, Long size, String fileType) {
        return QuestionImage.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .imageLocation(imageLocation)
                .size(size)
                .fileType(fileType)
                .status(ImageStatus.TEMPORARY)
                .savedAt(LocalDateTime.now())
                .build();

    }

    public void changeToSaved(){
        this.status = ImageStatus.SAVED;
    }
}
