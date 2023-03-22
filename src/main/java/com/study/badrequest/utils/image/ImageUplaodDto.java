package com.study.badrequest.utils.image;

import com.study.badrequest.domain.board.entity.BoardImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageUplaodDto {
    private String originalFileName;
    private String storedFileName;
    private String imageLocation;
    private Long size;
    private String fileType;

    @Builder
    public ImageUplaodDto(String originalFileName, String storedFileName, String imageLocation, Long size, String fileType) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageLocation = imageLocation;
        this.size = size;
        this.fileType = fileType;
    }

    /**
     * Entity -> DTO
     */
    public ImageUplaodDto(BoardImage boardImage) {
        this.originalFileName = boardImage.getOriginalFileName();
        this.storedFileName = boardImage.getStoredFileName();
        this.imageLocation = boardImage.getImageLocation();
        this.size = boardImage.getSize();
        this.fileType = boardImage.getFileType();
    }


}