package com.study.badrequest.service.board;


import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.board.BoardImage;
import com.study.badrequest.domain.board.BoardImageStatus;
import com.study.badrequest.dto.board.BoardImageResponse;
import com.study.badrequest.repository.board.BoardImageRepository;
import com.study.badrequest.utils.image.ImageUploadDto;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardImageServiceImpl implements BoardImageService {
    private final ImageUploader imageUploader;
    private final BoardImageRepository boardImageRepository;

    @Override
    public BoardImageResponse.Create save(MultipartFile image) {

        final String FOLDER_NAME = "board";

        ImageUploadDto imageUploadDto = imageUploader.uploadFile(image, FOLDER_NAME);

        BoardImage boardImage = BoardImage
                .createBoardImage()
                .originalFileName(imageUploadDto.getOriginalFileName())
                .storedFileName(imageUploadDto.getStoredFileName())
                .imageLocation(imageUploadDto.getImageLocation())
                .fileType(imageUploadDto.getFileType())
                .size(imageUploadDto.getSize())
                .build();

        BoardImage save = boardImageRepository.save(boardImage);
        return new BoardImageResponse.Create(save.getId(), save.getImageLocation());
    }

    @Override
    public void changeStatus(List<Long> ids, Board board, BoardImageStatus status) {
        if (ids != null) {
            boardImageRepository.findAllById(ids).forEach(image -> image.changeImageStatus(board, status));
        }
    }

    @Override
    public void update(List<Long> idsToModified, Board board) {

        //수정 요청된 이미지가 없을 경우 게시판의 이미지 전체 삭제
        if (deleteIfNoImageRequested(idsToModified, board)) return;
        //해당 게시판의 이미 저장된 이미지 조회
        List<BoardImage> imagesAlreadySaved = getBoardImageListAlreadySaved(board);
        //이미 저장된 이미지가 없다면 수정 요청된 상태가 변경되지 않은 이미지를 SAVED 로 상태 변경
        if (statusToSavedIfEmptyAlreadySaved(idsToModified, board, imagesAlreadySaved)) return;

        List<BoardImage> imagesNewlySaved = getBoardImageListIsTemporary(idsToModified);

        List<BoardImage> imagesToDelete = findImageToDelete(idsToModified, imagesAlreadySaved);

        boardImageRepository.deleteAll(imagesToDelete);

        imagesNewlySaved.removeAll(imagesToDelete);
        //이미지 상태 변경 -> SAVED
        imagesNewlySaved.forEach(image -> image.changeImageStatus(board, BoardImageStatus.SAVED));
    }

    private List<BoardImage> findImageToDelete(List<Long> idsToModified, List<BoardImage> imagesAlreadySaved) {
        return imagesAlreadySaved.stream()
                .filter(image -> !idsToModified.contains(image.getId()))
                .peek(image -> imageUploader.deleteFileByStoredNames(image.getStoredFileName()))
                .collect(Collectors.toList());
    }

    private List<BoardImage> getBoardImageListIsTemporary(List<Long> imageIds) {
        return boardImageRepository.findAllByIdInAndStatus(imageIds, BoardImageStatus.TEMPORARY);
    }

    private boolean statusToSavedIfEmptyAlreadySaved(List<Long> imageIds, Board board, List<BoardImage> imagesAlreadySaved) {
        if (imagesAlreadySaved.isEmpty()) {
            List<BoardImage> imageList = getBoardImageListIsTemporary(imageIds);
            imageList.forEach(image -> image.changeImageStatus(board, BoardImageStatus.SAVED));
            return true;
        }
        return false;
    }

    private List<BoardImage> getBoardImageListAlreadySaved(Board board) {
        return boardImageRepository.findAllByBoardAndStatus(board, BoardImageStatus.SAVED);
    }

    private boolean deleteIfNoImageRequested(List<Long> imageIds, Board board) {
        if (imageIds.isEmpty()) {
            // 요청에 이미지 아이디가 없으면 해당 게시물 이미지 전체 삭제
            deleteAllImageInBoard(board);
            return true;
        }
        return false;
    }

    private void deleteAllImageInBoard(Board board) {
        boardImageRepository.findAllByBoardAndStatus(board, BoardImageStatus.SAVED)
                .forEach(image -> {
                    imageUploader.deleteFileByStoredNames(image.getStoredFileName());
                    boardImageRepository.delete(image);
                });
    }


    @Override
    public void deleteByBoard(Board board) {
        List<BoardImage> imageList = getBoardImageListAlreadySaved(board);
        imageUploader.deleteFileByStoredNames(imageList.stream().map(BoardImage::getStoredFileName).collect(Collectors.toList()));
    }

    @Override
    @Scheduled(cron = "0 0 5 * * *")
    public void clearTemporaryImage() {
        log.info("[Clear Temporary Image START AT ={}]", LocalDateTime.now());
        List<BoardImage> boardImages = boardImageRepository.findByStatus(BoardImageStatus.TEMPORARY);

        if (!boardImages.isEmpty()) {
            imageUploader.deleteFileByStoredNames(boardImages.stream().map(BoardImage::getStoredFileName).collect(Collectors.toList()));
            boardImageRepository.deleteAll(boardImages);
        }

        log.info("[Clear Temporary Image FINISH AT ={}]", LocalDateTime.now());
    }
}
