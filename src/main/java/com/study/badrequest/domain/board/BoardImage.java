package com.study.badrequest.domain.board;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BOARD_IMAGE")
@Getter
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String imageLocation;
    private Long size;
    private String fileType;
    private BoardImageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    @org.hibernate.annotations.Index(name = "BOARD_IDX_IMAGE_BOARD")
    private Board board;

    @Builder(builderMethodName = "createBoardImage")
    public BoardImage(String originalFileName, String storedFileName, String imageLocation, Long size, String fileType) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.fileType = fileType;
        this.status = BoardImageStatus.TEMPORARY;
    }

    public void changeImageStatus(Board board, BoardImageStatus status) {
        this.board = board;
        this.status = status;
    }

}
