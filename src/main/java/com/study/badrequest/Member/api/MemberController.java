package com.study.badrequest.Member.api;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.service.MemberCommandService;
import com.study.badrequest.Member.dto.MemberRequestForm;
import com.study.badrequest.Member.dto.UpdateMemberForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.login.api.LoginController;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
@Slf4j
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping(value = "/member",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postMember(@RequestBody MemberRequestForm.CreateMember form) {
        log.info("[postMember]");
        Member member = memberCommandService.signupMember(form);

        URI location = linkTo(MemberCommandService.class).slash("/login").slash(member.getId()).toUri();
        Link loginLink = linkTo(LoginController.class).slash("/login").withRel("POST: 로그인");
        EntityModel<MemberSignupResult> model = EntityModel.of(new MemberSignupResult(member));
        model.add(loginLink);

        return ResponseEntity
                .created(location)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    @NoArgsConstructor
    @Getter
    public static class MemberSignupResult {
        private Long memberId;
        private LocalDateTime createdAt;

        public MemberSignupResult(Member member) {
            this.memberId = member.getId();
            this.createdAt = member.getCreatedAt();
        }
    }

    @PatchMapping("/member/{memberId}")
    public ResponseEntity patchMember(@PathVariable Long memberId, @RequestBody UpdateMemberForm form) {

        Member member = memberCommandService.updateMember(memberId, form);

        return ResponseEntity.ok().body(new ResponseForm.Of(CustomStatus.SUCCESS, new MemberPatchResult(member)));
    }

    @NoArgsConstructor
    @Getter
    public static class MemberPatchResult {
        private Long id;
        private LocalDateTime updatedAt;

        public MemberPatchResult(Member member) {
            this.id = member.getId();
            this.updatedAt = member.getUpdatedAt();
        }
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity deleteMember(@PathVariable Long memberId, String password) {
        log.info("[deleteMember]");

        memberCommandService.resignMember(memberId, password);

        return ResponseEntity.ok().body(new ResponseForm.Of(CustomStatus.SUCCESS, new MemberDeleteResult()));
    }

    @NoArgsConstructor
    @Getter
    public static class MemberDeleteResult {
        private String thanks = "이용해 주셔서 감사합니다.";

    }
}
