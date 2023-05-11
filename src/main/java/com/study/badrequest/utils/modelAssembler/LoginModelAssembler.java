package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.api.member.MemberQueryApiController;
import com.study.badrequest.commons.hateoas.ResponseModelAssembler;
import com.study.badrequest.dto.login.LoginResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoginModelAssembler {
    public EntityModel<LoginResponse.LoginResult> createLoginModel(LoginResponse.LoginResult result) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(result.getMemberId(), null)).withRel("login user information"),
                linkTo(methodOn(LoginController.class).logout(null, null)).withRel("logout"),
                linkTo(methodOn(LoginController.class).reIssue(null, null, null)).withRel("reIssue token")
        );

        return EntityModel.of(result).add(links);
    }

    public EntityModel<LoginResponse.LogoutResult> toModel(LoginResponse.LogoutResult result) {

        return EntityModel.of(result)
                .add(linkTo(LoginController.class).slash("/login").withRel("POST : 로그인"));
    }

    public EntityModel<LoginResponse.ReIssueResult> toModel(LoginResponse.ReIssueResult result) {

        return EntityModel.of(result);

    }
}
