package com.study.badrequest.domain.board.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    private String originalFileName;
    private String storedFileName;
    private String fullPath;
    private Long size;
    private String fileType;
    @Builder
    public BoardImage(Board board, String originalFileName, String storedFileName, String fullPath, Long size, String fileType) {
        this.board = board;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fullPath = fullPath;
        this.size = size;
        this.fileType = fileType;
    }
}
