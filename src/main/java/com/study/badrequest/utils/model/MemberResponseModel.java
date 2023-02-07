package com.study.badrequest.utils.model;

import com.study.badrequest.api.LoginController;
import com.study.badrequest.api.MemberController;
import com.study.badrequest.domain.Member.dto.MemberResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MemberResponseModel {

    public EntityModel<MemberResponse.SignupResult> toModel(MemberResponse.SignupResult result) {
        return EntityModel.of(result)
                .add(linkTo(LoginController.class).slash("/login").withRel("POST: 로그인"));
    }

    public EntityModel<MemberResponse.UpdateResult> toModel(MemberResponse.UpdateResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberController.class).getMember(result.getMemberId())).withRel("GET: 회원 정보"));
    }

    public EntityModel<MemberResponse.DeleteResult> toModel(MemberResponse.DeleteResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberController.class).postMember(null, null)).withRel("POST: 회원가입"));
    }


    public URI getUri(Long memberId) {
        return linkTo(MemberResponseModel.class).slash("/login").slash(memberId).toUri();
    }

}
