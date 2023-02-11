package com.study.badrequest.api.member;




import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.member.service.MemberCommandService;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.commons.exception.custom_exception.CustomValidationException;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
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


import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;


@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
@Slf4j
public class MemberController {
    private final MemberCommandService memberCommandService;
    private final MemberValidator memberValidator;
    private final MemberResponseModelAssembler memberResponseModelAssembler;



    @PostMapping(value = "/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> postMember(@Validated @RequestBody MemberRequest.CreateMember form, BindingResult bindingResult) {

        memberValidator.validateCreateForm(form);

        if (bindingResult.hasErrors()) {
            log.error("[postMember.validation error]");
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);

        EntityModel<MemberResponse.SignupResult> signupResultEntityModel = memberResponseModelAssembler.toModel(signupResult);

        return ResponseEntity
                .created(memberResponseModelAssembler.getUri(signupResult.getMemberId()))
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, signupResultEntityModel));
    }

    @PutMapping("/member/{memberId}/password")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> putPassword(@Validated @PathVariable Long memberId, @RequestBody MemberRequest.ResetPassword form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        MemberResponse.UpdateResult updateResult = memberCommandService.resetPassword(memberId, form.getPassword(), form.getNewPassword());

        EntityModel<MemberResponse.UpdateResult> updateResultEntityModel = memberResponseModelAssembler.toModel(updateResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, updateResultEntityModel));
    }

    @PutMapping(value = "/member/{memberId}/contact", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> putContact(@Validated @PathVariable Long memberId, @RequestBody MemberRequest.UpdateContact form, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        memberValidator.validateContact(form.getContact());

        MemberResponse.UpdateResult updateResult = memberCommandService.updateContact(memberId, form.getContact());

        EntityModel<MemberResponse.UpdateResult> updateResultEntityModel = memberResponseModelAssembler.toModel(updateResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, updateResultEntityModel));
    }

    @DeleteMapping("/member/{memberId}")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> deleteMember(@Validated @PathVariable Long memberId, @RequestBody MemberRequest.DeleteMember form, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        MemberResponse.DeleteResult deleteResult = memberCommandService.resignMember(memberId, form.getPassword());

        EntityModel<MemberResponse.DeleteResult> deleteResultEntityModel = memberResponseModelAssembler.toModel(deleteResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, deleteResultEntityModel));
    }


}
