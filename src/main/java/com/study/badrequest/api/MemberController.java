package com.study.badrequest.api;

import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.Member.domain.service.MemberCommandService;
import com.study.badrequest.domain.Member.dto.MemberRequestForm;
import com.study.badrequest.domain.Member.dto.MemberResponseForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.exception.custom_exception.CustomValidationException;
import com.study.badrequest.exception.custom_exception.MemberException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;


import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
@Slf4j
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberValidator memberValidator;

    @PostMapping(value = "/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postMember(@Validated @RequestBody MemberRequestForm.CreateMember form, BindingResult bindingResult) {
        log.info("[MemberController.postMember]");

        memberValidator.validateCreateForm(form);

        if (bindingResult.hasErrors()) {
            log.error("[postMember.validation error]");
            throw new CustomValidationException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        Member member = memberCommandService.signupMember(form);

        EntityModel<MemberResponseForm.SignupResult> model = EntityModel.of(new MemberResponseForm.SignupResult(member));
        model.add(linkTo(LoginController.class).slash("/login").withRel("POST: 로그인"));

        return ResponseEntity
                .created(linkTo(MemberCommandService.class).slash("/login").slash(member.getId()).toUri())
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    // TODO: 2023/01/06 test 
    @PutMapping("/member/{memberId}/password")
    public ResponseEntity putPassword(@Validated @PathVariable Long memberId, @RequestBody MemberRequestForm.ResetPassword form, BindingResult bindingResult) {
        log.info("[MemberController.patchMember]");
        if (bindingResult.hasErrors()) {
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        Member member = memberCommandService.resetPassword(memberId, form.getPassword(), form.getNewPassword());

        EntityModel<MemberResponseForm.UpdateResult> model = EntityModel.of(new MemberResponseForm.UpdateResult(member));
        model.add(linkTo(methodOn(MemberController.class).getMember(member.getId())).withRel("GET: 회원 정보"));

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, model));
    }

    @PutMapping(value = "/member/{memberId}/contact",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity putContact(@Validated @PathVariable Long memberId, @RequestBody MemberRequestForm.UpdateContact form, BindingResult bindingResult) {
        log.info("[MemberController.putContact]");

        if (bindingResult.hasErrors()) {
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        memberValidator.validateContact(form);

        Member member = memberCommandService.updateContact(memberId, form.getContact());
        EntityModel<MemberResponseForm.UpdateResult> model = EntityModel.of(new MemberResponseForm.UpdateResult(member));
        model.add(linkTo(methodOn(MemberController.class).getMember(member.getId())).withRel("GET: 회원 정보"));

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, model));
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity deleteMember(@Validated @PathVariable Long memberId, @RequestBody MemberRequestForm.DeleteMember form, BindingResult bindingResult) {
        log.info("[MemberController.deleteMember]");

        if(bindingResult.hasErrors()){
            throw new MemberException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        memberCommandService.resignMember(memberId, form.getPassword());
        EntityModel<MemberResponseForm.DeleteResult> model = EntityModel.of(new MemberResponseForm.DeleteResult());
        model.add(linkTo(MemberController.class).slash("/api").slash("/v1").slash("/member").withRel("POST: 회원가입"));

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, model));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity getMember(@PathVariable Long memberId) {


        return null;
    }


}
