package com.study.badrequest.api.member;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.member.service.MemberCommandService;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.commons.exception.custom_exception.CustomValidationException;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;


import static com.study.badrequest.commons.consts.CustomURL.BASE_API_VERSION_URL;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberCommandController {
    private final MemberCommandService memberCommandService;
    private final MemberValidator memberValidator;
    private final MemberResponseModelAssembler memberResponseModelAssembler;
    public final static String POST_MEMBER_URL = BASE_API_VERSION_URL + "/members";
    public final static String PATCH_MEMBER_PASSWORD_URL = BASE_API_VERSION_URL + "/members/{memberId}/password";
    public final static String PATCH_MEMBER_CONTACT_URL = BASE_API_VERSION_URL + "/members/{memberId}/contact";
    public final static String DELETE_MEMBER_URL = BASE_API_VERSION_URL + "/members/{memberId}";

    /**
     * @param form: String email, String password, String nickname, String contact
     * @return 201 created, memberId, createdAt
     */
    @PostMapping(value = POST_MEMBER_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> createMember(@Validated @RequestBody MemberRequest.CreateMember form, BindingResult bindingResult) {
        log.info("Create Member");

        memberValidator.emailAndContactDuplicateChack(form);

        throwValidationExceptionIfErrors(bindingResult);

        EntityModel<MemberResponse.Create> model = memberResponseModelAssembler.toModel(memberCommandService.signupMember(form));

        return ResponseEntity
                .created(memberResponseModelAssembler.getLocationUri(model.getContent().getId()))
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    @PatchMapping(value = PATCH_MEMBER_PASSWORD_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> patchPassword(@Validated @PathVariable Long memberId, @RequestBody MemberRequest.ResetPassword form, BindingResult bindingResult) {
        log.info("=>MemberCommendController->patchPassword");

        throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.UpdateResult updateResult = memberCommandService.resetPassword(memberId, form.getPassword(), form.getNewPassword());

        EntityModel<MemberResponse.UpdateResult> updateResultEntityModel = memberResponseModelAssembler.toModel(updateResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, updateResultEntityModel));
    }

    @PatchMapping(value = PATCH_MEMBER_CONTACT_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> patchContact(@Validated
                                                        @PathVariable Long memberId,
                                                        @RequestBody MemberRequest.UpdateContact form,
                                                        BindingResult bindingResult) {
        log.info("=>MemberCommendController->patchContact");

        throwValidationExceptionIfErrors(bindingResult);

        memberValidator.existContact(form.getContact());

        MemberResponse.UpdateResult updateResult = memberCommandService.updateContact(memberId, form.getContact());

        EntityModel<MemberResponse.UpdateResult> updateResultEntityModel = memberResponseModelAssembler.toModel(updateResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, updateResultEntityModel));
    }

    @DeleteMapping(DELETE_MEMBER_URL)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> deleteMember(@Validated @PathVariable Long memberId, @RequestBody MemberRequest.DeleteMember form, BindingResult bindingResult) {

        log.info("=>MemberCommendController->deleteMember");

        throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.DeleteResult deleteResult = memberCommandService.resignMember(memberId, form.getPassword());

        EntityModel<MemberResponse.DeleteResult> deleteResultEntityModel = memberResponseModelAssembler.toModel(deleteResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, deleteResultEntityModel));
    }

    private void throwValidationExceptionIfErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Member Validation Error");
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }
    }


}
