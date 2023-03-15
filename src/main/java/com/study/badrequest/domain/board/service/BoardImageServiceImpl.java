package com.study.badrequest.domain.board.service;

import com.study.badrequest.domain.board.dto.BoardImageResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.entity.BoardImageStatus;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.utils.image.ImageDetailDto;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
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

        ImageDetailDto imageDetailDto = imageUploader.uploadFile(image, FOLDER_NAME);

        BoardImage boardImage = BoardImage
                .createBoardImage()
                .originalFileName(imageDetailDto.getOriginalFileName())
                .storedFileName(imageDetailDto.getStoredFileName())
                .imageLocation(imageDetailDto.getFullPath())
                .fileType(imageDetailDto.getFileType())
                .size(imageDetailDto.getSize())
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
    public void update(List<Long> imageIds, Board board) {

        if (imageIds == null) {
            // 요청에 이미지 아이디가 없으면 해당 게시물 이미지 전체 삭제
            boardImageRepository.findByBoard(board).forEach(image -> {
                imageUploader.deleteFile(image.getStoredFileName());
                boardImageRepository.delete(image);
            });
            return;
        }

        List<BoardImage> savedInBoard = boardImageRepository.findByBoard(board);

        List<BoardImage> findRequestedImage = boardImageRepository.findAllById(imageIds);

        List<BoardImage> targetToDelete = new ArrayList<>();

        savedInBoard.forEach(saved -> {

            if (!imageIds.contains(saved.getId())) {
                // 저장된 이미지 중에 요청된 이미지와 일치하지 않는 것은 삭제
                imageUploader.deleteFile(saved.getStoredFileName());
                targetToDelete.add(saved);
            }
        });

        boardImageRepository.deleteAll(targetToDelete);

        findRequestedImage.removeAll(targetToDelete); // 차집합

        findRequestedImage.forEach(image -> image.changeImageStatus(board, BoardImageStatus.SAVED));
    }

    @Override
    public void deleteByBoard(Board board) {
        List<BoardImage> imageList = boardImageRepository.findByBoard(board);
        imageUploader.deleteFile(imageList.stream().map(BoardImage::getStoredFileName).collect(Collectors.toList()));
    }
    @Override
    @Scheduled(cron = "0 0 5 * * *")
    public void clearTemporaryImage() {
        log.info("[Clear Temporary Image START AT ={}]", LocalDateTime.now());
        List<BoardImage> boardImages = boardImageRepository.findByStatus(BoardImageStatus.TEMPORARY);

        if (!boardImages.isEmpty()) {
            imageUploader.deleteFile(boardImages.stream().map(BoardImage::getStoredFileName).collect(Collectors.toList()));
            boardImageRepository.deleteAll(boardImages);
        }

        log.info("[Clear Temporary Image FINISH AT ={}]", LocalDateTime.now());
    }
}
