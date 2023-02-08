package com.study.badrequest.domain.board.service;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.exception.custom_exception.BoardException;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardCommandService {
    public static final String BOARD_FOLDER_NAME = "board";
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final ImageUploader imageUploader;
    private final CommentRepository commentRepository;

    /**
     * 게시판 생성
     */
    @CustomLogTracer
    public BoardResponse.Create create(BoardRequest.Create form, List<MultipartFile> images) {

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));

        Board board = Board.createBoard()
                .title(form.getTitle())
                .contents(form.getContents())
                .topic(form.getTopic())
                .category(form.getCategory())
                .member(member)
                .build();

        saveImages(images, board);

        Board save = boardRepository.save(board);

        return new BoardResponse.Create(save.getId(), save.getCreatedAt());
    }

    @CustomLogTracer
    public BoardResponse.Update update(Long boardId, BoardRequest.Update form, List<MultipartFile> images) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));

        deleteImages(images, board);

        saveImages(images, board);

        board.update(form.getTitle(), form.getContents(), form.getCategory(), form.getTopic());

        Board updated = getBoard(boardId);

        return new BoardResponse.Update(updated);
    }

    @CustomLogTracer
    public void delete(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<BoardImage> images = boardImageRepository.findByBoard(board);
        if (!images.isEmpty()) {
            imageUploader.deleteFile(images.stream().map(BoardImage::getStoredFileName).collect(Collectors.toList()));
            boardImageRepository.deleteAll(images);
        }

        List<Comment> comments = commentRepository.findAllByBoard(board);
        if (!comments.isEmpty()) {
            commentRepository.deleteAll(comments);
        }
        boardRepository.delete(board);
    }
    @CustomLogTracer
    public void saveImages(List<MultipartFile> images, Board board) {
        if (images != null) {

            List<BoardImage> boardImages = imageUploader.uploadFile(images, BOARD_FOLDER_NAME)
                    .stream()
                    .map(image ->
                            BoardImage.builder()
                                    .originalFileName(image.getOriginalFileName())
                                    .storedFileName(image.getStoredFileName())
                                    .fullPath(image.getFullPath())
                                    .size(image.getSize())
                                    .fileType(image.getFileType())
                                    .board(board)
                                    .build()
                    ).collect(Collectors.toList());

            boardImageRepository.saveAll(boardImages);
        }
    }

    @CustomLogTracer
    public void deleteImages(List<MultipartFile> images, Board board) {
        if (images == null) {
            boardImageRepository.findByBoard(board)
                    .stream()
                    .map(BoardImage::getStoredFileName)
                    .forEach(imageUploader::deleteFile);
        }
    }
    @CustomLogTracer
    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));
    }

    @CustomLogTracer
    public void deleteAll(List<Long> boardId) {
        boardRepository.findAllById(boardId);

    }
}
