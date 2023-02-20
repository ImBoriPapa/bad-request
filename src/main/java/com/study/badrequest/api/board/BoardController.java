package com.study.badrequest.api.board;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.service.BoardCommandServiceImpl;
import com.study.badrequest.commons.exception.custom_exception.CustomValidationException;
import com.study.badrequest.utils.modelAssembler.BoardResponseModelAssembler;
import com.study.badrequest.utils.validator.BoardValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class BoardController {
    private final BoardCommandServiceImpl boardCommandService;
    private final BoardValidator boardValidator;
    private final BoardResponseModelAssembler boardResponseModelAssembler;

    // TODO: 2023/02/09 ReqeustPart는 Valid 작동안됨 -> boardValidator 구현
    @PostMapping("/board")
    @CustomLogTracer
    public ResponseEntity postBoard(@Valid
                                    @AuthenticationPrincipal User user,
                                    @RequestPart(value = "form", required = true) BoardRequest.Create form,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        if (user.getUsername() == null) {
            throw new MemberException(CustomStatus.ALREADY_LOGOUT);
        }

        BoardResponse.Create create = boardCommandService.create(user.getUsername(), form, images);

        EntityModel<BoardResponse.Create> entityModel = boardResponseModelAssembler.toModel(create);

        return ResponseEntity
                .created(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).toUri())
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    /**
     * memberId 필수
     * 각각 수정값이 없을 경우 기존 데이터 유지
     * title
     * contents;
     * topic;
     * category;
     * images;
     */
    @PatchMapping("/board/{boardId}")
    public ResponseEntity patchBoard(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "boardId") Long boardId,
            @RequestPart(name = "form") BoardRequest.Update form,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) {

        boardValidator.validateUpdateForm(form);

        BoardResponse.Update update = boardCommandService.update(user.getUsername(), boardId, form, images);

        EntityModel<BoardResponse.Update> entityModel = boardResponseModelAssembler.toModel(update);

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    // TODO: 2023/02/15 Delete

}
