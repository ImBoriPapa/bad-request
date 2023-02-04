package com.study.badrequest.domain.board.service;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.Member.domain.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.repository.CommentRepository;
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
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final ImageUploader imageUploader;
    private final CommentRepository commentRepository;

    /**
     * 게시판 생성
     */
    @CustomLogger
    public BoardResponse.Create create(BoardRequest.Create form, List<MultipartFile> images) {

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Board board = Board.createBoard()
                .title(form.getTitle())
                .contents(form.getContext())
                .topic(form.getTopic())
                .category(form.getCategory())
                .member(member)
                .build();

        saveImages(images, board);

        Board save = boardRepository.save(board);

        return new BoardResponse.Create(save.getId(), save.getCreatedAt());
    }

    @CustomLogger
    public void saveImages(List<MultipartFile> images, Board board) {
        if (images != null) {

            List<BoardImage> boardImages = imageUploader.uploadFile(images, "board")
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

    @CustomLogger
    public void update(Long boardId) {

    }

    @CustomLogger
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

    @CustomLogger
    public void deleteAll(List<Long> boardId) {
        boardRepository.findAllById(boardId);

    }
}
