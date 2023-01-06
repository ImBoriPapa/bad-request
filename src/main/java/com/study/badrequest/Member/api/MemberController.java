package com.study.badrequest.Member.api;

import com.study.badrequest.Member.api.validator.MemberValidator;
import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.service.MemberCommandService;
import com.study.badrequest.Member.dto.MemberRequestForm;
import com.study.badrequest.Member.dto.MemberResponseForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.exception.custom_exception.CustomValidationException;
import com.study.badrequest.login.api.LoginController;

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

        memberValidator.validate(form);

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
    public ResponseEntity putPassword(@PathVariable Long memberId, @RequestBody MemberRequestForm.ResetPassword form) {
        log.info("[MemberController.patchMember]");
        Member member = memberCommandService.resetPassword(memberId, form.getPassword(), form.getNewPassword());
        
        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, new MemberResponseForm.UpdateResult(member)));
    }

    @PutMapping("/member/{memberId}/contact")
    public ResponseEntity putContact(@PathVariable Long memberId, @RequestBody MemberRequestForm.UpdateContact form) {
        log.info("[MemberController.putContact]");
        Member member = memberCommandService.updateContact(memberId, form.getContact());

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, new MemberResponseForm.UpdateResult(member)));
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity deleteMember(@PathVariable Long memberId, String password) {
        log.info("[MemberController.deleteMember]");

        memberCommandService.resignMember(memberId, password);

        return ResponseEntity.ok().body(new ResponseForm.Of(CustomStatus.SUCCESS, new MemberResponseForm.DeleteResult()));
    }


}
