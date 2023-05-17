package com.study.badrequest.api.board;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.board.BoardRequest;
import com.study.badrequest.dto.board.BoardResponse;
import com.study.badrequest.exception.custom_exception.BasicCustomValidationException;
import com.study.badrequest.service.board.BoardCommandServiceImpl;
import com.study.badrequest.utils.modelAssembler.BoardResponseModelAssembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.study.badrequest.commons.constants.ApiURL.BASE_API_VERSION_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_API_VERSION_URL)
public class BoardController {
    private final BoardCommandServiceImpl boardCommandService;

    private final BoardResponseModelAssembler boardResponseModelAssembler;


    @PostMapping("/board")
    @CustomLogTracer
    public ResponseEntity postBoard(@Valid
                                    @LoggedInMember CurrentLoggedInMember.Information information,
                                    @RequestBody BoardRequest.Create form,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicCustomValidationException(ApiResponseStatus.VALIDATION_ERROR, bindingResult);
        }

        BoardResponse.Create create = boardCommandService.create(information.getId(),information.getAuthority() ,form);

        EntityModel<BoardResponse.Create> entityModel = boardResponseModelAssembler.toModel(create);

        return ResponseEntity
                .created(linkTo(BoardController.class).slash("/board").slash(create.getBoardId()).toUri())
                .body(new ApiResponse.Success<>(ApiResponseStatus.SUCCESS, entityModel));
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
            @RequestPart(name = "form") BoardRequest.Update form) {



        BoardResponse.Update update = boardCommandService.update(user, boardId, form);

        EntityModel<BoardResponse.Update> entityModel = boardResponseModelAssembler.toModel(update);

        return ResponseEntity
                .ok()
                .body(new ApiResponse.Success<>(ApiResponseStatus.SUCCESS, entityModel));
    }

    // TODO: 2023/02/15 Delete
    @DeleteMapping("/board/{boardId}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable(name = "boardId") Long boardId) {

        boardCommandService.delete(user, boardId);

    }

}
