package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.LoginController;
import com.study.badrequest.api.member.MemberController;
import com.study.badrequest.api.member.MemberQueryController;
import com.study.badrequest.domain.member.dto.MemberAuthDto;
import com.study.badrequest.domain.member.dto.MemberResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MemberResponseModelAssembler {

    public EntityModel<MemberResponse.SignupResult> toModel(MemberResponse.SignupResult result) {
        return EntityModel.of(result)
                .add(linkTo(LoginController.class).slash("/login").withRel("POST: 로그인"));
    }

    public EntityModel<MemberResponse.UpdateResult> toModel(MemberResponse.UpdateResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberQueryController.class).getMember(null,result.getMemberId())).withRel("GET: 회원 정보"));
    }

    public EntityModel<MemberResponse.DeleteResult> toModel(MemberResponse.DeleteResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberController.class).postMember(null, null)).withRel("POST: 회원가입"));
    }

    // TODO: 2023/02/11 응답값 추가
    public EntityModel<MemberResponse.AuthResult> toModel(MemberAuthDto result) {
        return EntityModel.of(new MemberResponse.AuthResult(result.getId(), result.getAuthority()));

    }

    public URI getUri(Long memberId) {
        return linkTo(MemberResponseModelAssembler.class).slash("/login").slash(memberId).toUri();
    }

}
